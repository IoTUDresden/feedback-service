package de.tud.feedback.plugin;

import de.tud.feedback.plugin.domain.NeoDevice;
import de.tud.feedback.plugin.domain.NeoPeer;
import de.tud.feedback.plugin.domain.NeoPeerMetric;
import de.tud.feedback.plugin.domain.NeoProcess;
import de.tud.feedback.plugin.events.ProteusEvent.*;
import de.tud.feedback.plugin.repository.NeoDeviceRepository;
import de.tud.feedback.plugin.repository.NeoPeerMetricRepository;
import de.tud.feedback.plugin.repository.NeoPeerRepository;
import de.tud.feedback.plugin.repository.NeoProcessRepository;
import eu.vicci.process.client.ProcessEngineClientBuilder;
import eu.vicci.process.client.core.ConnectionListener;
import eu.vicci.process.client.core.IProcessEngineClient;
import eu.vicci.process.distribution.core.PeerProfile;
import eu.vicci.process.distribution.core.SuperPeerRequest;
import eu.vicci.process.model.util.configuration.TopicId;
import eu.vicci.process.model.util.messages.core.IMessageReceiver;
import eu.vicci.process.model.util.messages.core.IStateChangeMessage;
import eu.vicci.process.model.util.messages.core.PeerMetrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * This agent monitors all interesting stuff from proteus (e.g. state changes, new peers).
 * Note: PROtEUS can handle only one peer per ip address.
 * <p>
 * The monitor runs in a single thread. All operations from other threads should added to the event queue.
 * This will (hopefully) ensure that there are no sync problems.
 * <p>
 * TODO question over questions:
 * - how handle disconnects from peers?
 * -> just marked as disconnected/connected in the repo
 * <p>
 * - do we need really a heartbeat? (wamp maybe can track this)
 * - proteus needs registering at resource on the fb service
 * <p>
 * - how to handle disconnects from superpeer?
 * -> we do nothing and just wait till the client has reconnected (maybe messages are lost?)
 * -> if the superpeer was restarted, it will send a new request for connection - so we know it was restarted and we can handle all the stuff
 * <p>
 * - how to handle new requests from superpeer (e.g. we are connected to the super peer and the same is requesting another times?)
 * -> we disconnect from the old, delete all processes which belongs to the old super (NeoPeer should be null for all of those) and connect to the new one.
 * <p>
 * - what is done with the peers after connection to a new peer?
 * -> after connect we get all peers and add them or not
 * <p>
 * - superpeers must send the connection profile (e.g. port, realm, namespace)
 * - adding metrics e.g. pubsub (less priority because easy)
 * - is it better to use transactions for accessing the graph repos?
 * - finally - how to handle compensation probably?
 * <p>
 * - polling or event based? (poll client.getConnectedPeers)
 * -> event based is cooler
 */
public class ProteusMonitorAgent extends ProteusMonitorBase {
    private final NeoProcessRepository processRepository;
    private final NeoPeerRepository peerRepository;
    private final NeoPeerMetricRepository metricRepository;
    private final NeoDeviceRepository deviceRepository;

    private ProcessIdFormatter idFormatter = new ProcessIdFormatter();

    private IProcessEngineClient client;
    private PeerProfile currentSuperPeer;
    private ConnectSettings connectSettings;

    public ProteusMonitorAgent(HealingPlugin healingPlugin) {
        processRepository = healingPlugin.getNeoProcessRepository();
        peerRepository = healingPlugin.getNeoPeerRepository();
        metricRepository = healingPlugin.getNeoPeerMetricRepository();
        deviceRepository = healingPlugin.getNeoDeviceRepository();
    }

    @Override
    protected void initMonitor() {
        //TODO NP is thrown
//        processRepository.deleteAll();
//        peerRepository.deleteAll();
    }

    //tracks connection state of the proteus client
    private ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void onConnect() {
            LOG.debug("proteus monitor connected to proteus");
            //should we do something?
        }

        @Override
        public void onDisconnect() {
            LOG.debug("proteus monitor disconnected from proteus");
            //should we do something?
        }
    };

    private IMessageReceiver<PeerMetrics> metricsListener = new IMessageReceiver<PeerMetrics>() {
        @Override
        public void onMessage(PeerMetrics peerMetrics) {
            addEvent(new PeerMetricsEvent(peerMetrics));
        }
    };

    /**
     * The peer is just marked as disconnected in that case
     *
     * @param profile
     */
    public void peerDisconnected(PeerProfile profile) {
        addEvent(new PeerDisconnectedEvent(profile));
    }

    /**
     * The peer is added if not exists ({@link #addPeerIfNotPresent(PeerProfile)})
     * and it {@link NeoPeer#isConnected} is set to true.
     *
     * @param profile
     */
    public void peerConnected(PeerProfile profile) {
        addEvent(new PeerConnectedEvent(profile));
    }


    /**
     * If a new SuperPeer is requesting,
     * we close the client and remove all processes which are executed on the super peer.
     * Then we make a new connection to the new super peer and get all peers again.
     * If new requesting super peer and the old super peer are equal (ids are equal) then we do nothing,
     * as the client will indefinitely try to connect to the super peer (default should be something around 3 seconds).
     *
     * @param request
     */
    public void superPeerIsRequesting(SuperPeerRequest request) {
        addEvent(new NewSuperPeerEvent(request));
    }

    public ConnectSettings getCurrentConnectionSettings() {
        return connectSettings;
    }

    ///////////////////////////////////////////////////////////////////////
    ///////////////// EventImplementation
    ////////////////////////////////////////////////////////////////////////

    boolean firstConnect = true;

    @Override
    protected void handleNewSuperPeer(NewSuperPeerEvent event) {
        SuperPeerRequest request = event.getRequest();

        //TODO workaround since the run method is not called but we need a clean repo at startup
        if (firstConnect) {
            processRepository.deleteAll();
            peerRepository.deleteAll();
            metricRepository.deleteAll();
            deviceRepository.deleteAll();
            firstConnect = false;
        }

        if (!checkSuperPeerArgs(request)) {
            LOG.error("invalid peer profile for super peer");
            return;
        }

        if (!superPeerHasChanged(request.profile))
            return;

        removeSuperPeerData();

        if (client != null) client.close();
        currentSuperPeer = request.profile;
        connectSettings = ConnectSettings.fromRequest(request);
        client = null;
        connect(request);
        addPeerIfNotPresent(currentSuperPeer);

        LOG.info("monitoring new super peer on '{}'", currentSuperPeer.getIp());

        client.getRegisteredPeers().forEach(p -> addPeerIfNotPresent(p));
        registerListeners();
    }

    @Override
    protected void handlePeerDisconnected(PeerDisconnectedEvent event) {
        PeerProfile profile = event.getProfile();
        NeoPeer peer = peerRepository.findByPeerId(profile.getPeerId());
        if (peer == null) return;
        peer.setConnected(false);
        peerRepository.save(peer);
        LOG.info("peer on '{}' has disconnected", peer.getIp());
    }

    @Override
    protected void handlePeerConnected(PeerConnectedEvent event) {
        PeerProfile profile = event.getProfile();
        handlePeerConnected(profile);
    }

    private void handlePeerConnected(PeerProfile profile) {
        addPeerIfNotPresent(profile);
        NeoPeer peer = peerRepository.findByPeerId(profile.getPeerId());
        peer.setConnected(true);
        peerRepository.save(peer);
        LOG.info("peer on '{}' has connected", peer.getIp());
    }

    /**
     * Updates the state of a process. The process is created and added to the repo if it not exists.
     *
     * @param event
     */
    @Override
    protected void handleStateChange(StateChangeEvent event) {
        IStateChangeMessage message = event.getMessage();
        String id = idFormatter.formatId(message.getPeerId(), message.getInstanceId());
        Iterable<NeoProcess> test = processRepository.findAll();
        NeoProcess process = processRepository.findByProcessId(id);
        if (process == null)
            process = createProcessFrom(message, id);
        process.setState(message.getState().toString());
        processRepository.save(process);
    }

    @Override
    protected void handlePeerMetrics(PeerMetricsEvent event) {
        PeerMetrics metrics = event.getMetrics();
        NeoPeer peer = peerRepository.findByPeerId(metrics.peerId);

        if (peer == null) {
            client.getRegisteredPeers().forEach(profile -> handlePeerConnected(profile));
            peer = peerRepository.findByPeerId(metrics.peerId);
        }

        if (peer == null) {
            LOG.error("unknown peer: {}", metrics.peerId);
            return;
        }

        NeoPeerMetric existingMetric = peer.getMetrics();
        if (existingMetric == null) {
            existingMetric = new NeoPeerMetric();
            peer.setMetrics(existingMetric);
        }

        peer.setLastHeartbeat(new Date());
        peer.setConnected(true);
        copyMetric(metrics, existingMetric);
        peerRepository.save(peer);
    }

    private static void copyMetric(PeerMetrics newMetric, NeoPeerMetric exisitingMetric) {
        exisitingMetric.setBatteryValue(newMetric.batteryStatus);
        exisitingMetric.setHasBattery(newMetric.hasBattery);
    }

    ///////////////////////////////////////////////////////////////////////
    ///////////////// Helper
    ////////////////////////////////////////////////////////////////////////

    private NeoProcess createProcessFrom(IStateChangeMessage message, String id) {
        NeoProcess process = new NeoProcess();
        process.setProcessId(id);
        process.setName(message.getProcessName());

        String peerId;
        if (runsOnPeer(message))
            peerId = message.getPeerId();
        else
            peerId = currentSuperPeer.getPeerId();

        NeoPeer peer = peerRepository.findByPeerId(peerId);
        if (peer == null) {
            client.getRegisteredPeers().forEach(profile -> addPeerIfNotPresent(profile));
            peer = peerRepository.findByPeerId(peerId);
        }

        if (isSubProcess(message)) {
            NeoProcess root = findRootProcess(message);
            if (root != null)
                process.setRoot(root);
            else
                LOG.error("cant find root process for '{}'", message.getProcessName());
        }

        if (peer == null)
            LOG.error("cant find peer '{}' for process '{}'", peerId, id);
        else
            process.setPeer(peer);

        return process;
    }

    private NeoProcess findRootProcess(IStateChangeMessage message) {
        String id = idFormatter.formatId(message.getPeerId(), message.getProcessInstanceId());
        NeoProcess process = processRepository.findByProcessId(id);
        return process;
    }

    private boolean isSubProcess(IStateChangeMessage message) {
        return !message.getProcessInstanceId().equals(message.getInstanceId());
    }

    /**
     * Adding a peer, if it not exists in the repo.
     * If we can assume that a peer was restarted (e.g. the ip of the existing peer and
     * the ip of the new peer are equal but the ids of the peers differ),
     * then all old processes and the old peer are removed from the repo and the new peer is added.
     *
     * @param peerProfile
     */
    private void addPeerIfNotPresent(PeerProfile peerProfile) {
        if (peerIsAlreadyTracked(peerProfile))
            return;

        NeoPeer peer = new NeoPeer();
        peer.setPeerId(peerProfile.getPeerId());
        peer.setName(peerProfile.getHostName());
        peer.setIp(peerProfile.getIp());
        peer.setSuperPeer(peerProfile.isSuperPeer());
        findOrCreateDevices(peerProfile).forEach(d -> peer.addDevice(d));
        peer.setConnected(true);
        peer.setLastHeartbeat(new Date());
        peerRepository.save(peer);
        LOG.info("peer on '{}' is monitored", peer.getIp());
    }

    /**
     * Tries to find the devices which are committed with the profile.
     * If the device is not found in the repo, it is added.
     */
    private List<NeoDevice> findOrCreateDevices(PeerProfile profile) {
        List<NeoDevice> devices = new ArrayList<>();
        if (profile.getDevices() == null || profile.getDevices().isEmpty())
            return devices;
        profile.getDevices().forEach(d -> findOrCreateDevice(d, devices));
        return devices;
    }

    private void findOrCreateDevice(String deviceId, List<NeoDevice> listToAdd) {
        NeoDevice tmp = deviceRepository.findByDeviceId(deviceId);
        if (tmp == null) {
            tmp = new NeoDevice();
            tmp.setDeviceId(deviceId);
            deviceRepository.save(tmp);
        }
        listToAdd.add(tmp);
    }

    //also removes instances from restarted peers
    private boolean peerIsAlreadyTracked(PeerProfile peerProfile) {
        NeoPeer existingPeer = peerRepository.findByPeerId(peerProfile.getPeerId());
        boolean hasSameIdAndIp = existingPeer != null && existingPeer.getIp().equals(peerProfile.getIp());

        if (hasSameIdAndIp) return true;

        existingPeer = peerRepository.findByIp(peerProfile.getIp());

        if (existingPeer == null) return false;

        //from this point, we assume the (old tracked) peer has been restarted and so get a new id,
        //so we have to delete all process data from the old peer and the peer itself

        deleteOldPeerDataFor(existingPeer);
        return false;
    }

    private void removeSuperPeerData() {
        if (currentSuperPeer == null) return;
        processRepository.delete(processRepository.findByPeerId(currentSuperPeer.getPeerId()));
        peerRepository.delete(currentSuperPeer.getPeerId());
    }

    private boolean superPeerHasChanged(PeerProfile newRequest) {
        if (currentSuperPeer == null) return true;
        return !currentSuperPeer.getPeerId().equals(newRequest.getPeerId());
    }

    private void deleteOldPeerDataFor(NeoPeer peer) {
        processRepository.delete(processRepository.findByPeerId(peer.getPeerId()));
        peerRepository.delete(peer.getPeerId());
    }

    private boolean runsOnPeer(IStateChangeMessage message) {
        return message.getPeerId() != null && !message.getPeerId().isEmpty();
    }

    private boolean connectedToSuperPeer() {
        return client != null && client.isConnected();
    }

    private boolean checkPeerArg(PeerProfile profile) {
        return profile.getIp() != null
                && !profile.getIp().isEmpty()
                && profile.getPeerId() != null
                && !profile.getPeerId().isEmpty();
    }

    private boolean checkSuperPeerArgs(SuperPeerRequest request) {
        PeerProfile profile = request.profile;
        return profile != null
                && request.namespace != null && !request.namespace.isEmpty()
                && request.port != null && !request.port.isEmpty()
                && request.realm != null && !request.realm.isEmpty()
                && profile.isSuperPeer()
                && checkPeerArg(profile);
    }

    private boolean connect(SuperPeerRequest request) {
        client = new ProcessEngineClientBuilder()
                .withIp(request.profile.getIp())
                .withPort(request.port)
                .withRealmName(request.realm)
                .withNamespace(request.namespace)
                .withName(ProteusMonitorAgent.class.getSimpleName())
                .build();
        return client.connect();
    }

    private void registerListeners() {
        client.addConnectionListener(connectionListener);
        client.addStateChangeListener(message -> addEvent(new StateChangeEvent(message)));
        client.subscribeToTopic(TopicId.PEER_METRICS, metricsListener, PeerMetrics.class);
    }

    public static class ConnectSettings {
        public String ip;
        public String port;
        public String realm;
        public String namespace;

        @Override
        public String toString() {
            return ip + ":" + port + ":" + realm + ":" + namespace;
        }

        private static ConnectSettings fromRequest(SuperPeerRequest request) {
            ConnectSettings settings = new ConnectSettings();
            settings.ip = request.profile.getIp();
            settings.port = request.port;
            settings.namespace = request.namespace;
            settings.realm = request.realm;
            return settings;
        }
    }
}
