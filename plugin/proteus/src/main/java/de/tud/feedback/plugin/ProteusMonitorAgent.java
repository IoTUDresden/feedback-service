package de.tud.feedback.plugin;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.plugin.domain.NeoPeer;
import de.tud.feedback.plugin.domain.NeoProcess;
import de.tud.feedback.plugin.repository.NeoPeerRepository;
import de.tud.feedback.plugin.repository.NeoProcessRepository;
import eu.vicci.process.client.ProcessEngineClientBuilder;
import eu.vicci.process.client.core.ConnectionListener;
import eu.vicci.process.client.core.IProcessEngineClient;
import eu.vicci.process.distribution.core.PeerProfile;
import eu.vicci.process.distribution.core.SuperPeerRequest;
import eu.vicci.process.model.util.messages.core.IStateChangeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This agent monitors all interesting stuff from proteus (e.g. state changes, new peers).
 * Note: PROtEUS can handle only one peer per ip address.
 *
 * TODO question over questions:
 * - how handle disconnects from peers?
 *   -> just marked as disconnected/connected in the repo
 *
 * - do we need really a heartbeat? (wamp maybe can track this)
 * - proteus needs registering at resource on the fb service
 *
 * - how to handle disconnects from superpeer?
 *  -> we do nothing and just wait till the client has reconnected (maybe messages are lost?)
 *  -> if the superpeer was restarted, it will send a new request for connection - so we know it was restarted and we can handle all the stuff
 *
 * - how to handle new requests from superpeer (e.g. we are connected to the super peer and the same is requesting another times?)
 *  -> we disconnect from the old, delete all processes which belongs to the old super (NeoPeer should be null for all of those) and connect to the new one.
 *
 *  - what is done with the peers after connection to a new peer?
 *   -> after connect we get all peers and add them or not
 *
 * - superpeers must send the connection profile (e.g. port, realm, namespace)
 * - adding metrics e.g. pubsub (less priority because easy)
 * - is it better to use transactions for accessing the graph repos?
 * - finally - how to handle compensation probably?
 *
 * - polling or event based? (poll client.getConnectedPeers)
 *   -> event based is cooler
 */
public class ProteusMonitorAgent implements ProcessMonitorAgent {
    private static final Logger LOG = LoggerFactory.getLogger(ProteusMonitorAgent.class);

    private final NeoProcessRepository processRepository;
    private final NeoPeerRepository peerRepository;

    private ProcessIdFormatter idFormatter = new ProcessIdFormatter();

    private IProcessEngineClient client;
    private PeerProfile currentSuperPeer;

    public ProteusMonitorAgent(NeoProcessRepository processRepository, NeoPeerRepository peerRepository){
        this.processRepository = processRepository;
        this.peerRepository = peerRepository;
    }

    @Override
    public void workWith(ContextUpdater updater) {
        // not used as we dont use the current available dogont updater
    }

    @Override
    public void run() {
        //clear once at startup

        //FIXME is the run method called?

        processRepository.deleteAll();
        peerRepository.deleteAll();

//        SuperPeerConfig config = getTestConfig();
//        if(!connect(config))
//            throw new RuntimeException("Cant connect to superpeer");
//
//        registerListeners();

        //TODO how do we get, that the runtime is stopped, so that we can close the client?
    }

    //tracks connection state of the proteus client
    private ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void onConnect() {
            //should we do something?
        }

        @Override
        public void onDisconnect() {
            //should we do something?
        }
    };

    /**
     * Updates the state of a process. The process is created and added to the repo if it not exists.
     * @param message
     */
    private void updateProcessStateFrom(IStateChangeMessage message){
        String id = idFormatter.formatId(message.getPeerId(), message.getInstanceId());
        Iterable<NeoProcess> test = processRepository.findAll();
        NeoProcess process = processRepository.findByProcessId(id);
        if(process == null)
            process = createProcessFrom(message, id);
        process.setState(message.getState().toString());
        processRepository.save(process);
    }

    private NeoProcess createProcessFrom(IStateChangeMessage message, String id){
        NeoProcess process = new NeoProcess();
        process.setProcessId(id);
        process.setName(message.getProcessName());
        if(runsOnPeer(message))
            process.setPeer(peerRepository.findByPeerId(message.getPeerId()));
        return process;
    }

    /**
     * Adding a peer, if it not exists in the repo.
     * If we can assume that a peer was restarted (e.g. the ip of the existing peer and
     * the ip of the new peer are equal but the ids of the peers differ),
     * then all old processes and the old peer are removed from the repo and the new peer is added.
     *
     * @param peerProfile
     */
    private void addPeerIfNotPresent(PeerProfile peerProfile){
        if(peerIsAlreadyTracked(peerProfile))
            return;

        NeoPeer peer = new NeoPeer();
        peer.setName(peerProfile.getHostName());
        peer.setIp(peerProfile.getIp());
        peer.setSuperPeer(false);
        peer.setConnected(true);
//        peer.setLastHeartbeat(LocalDateTime.now());
        peerRepository.save(peer);
        LOG.info("peer on '{}' is monitored", peer.getIp());
    }

    //also removes instances from restarted peers
    private boolean peerIsAlreadyTracked(PeerProfile peerProfile){
        NeoPeer existingPeer = peerRepository.findByPeerId(peerProfile.getPeerId());
        boolean hasSameIdAndIp = existingPeer != null && existingPeer.getIp().equals(peerProfile.getIp());

        if(hasSameIdAndIp) {
//            existingPeer.setLastHeartbeat(LocalDateTime.now());
            return true;
        }

        existingPeer = peerRepository.findByIp(peerProfile.getIp());

        if(existingPeer == null) return false;

        //from this point, we assume the (old tracked) peer has been restarted and so get a new id,
        //so we have to delete all process data from the old peer and the peer itself

        deleteOldPeerDataFor(existingPeer);
        return false;
    }

    /**
     * The peer is just marked as disconnected in that case
     * @param profile
     */
    public void peerDisoconnected(PeerProfile profile){
        NeoPeer peer = peerRepository.findByPeerId(profile.getPeerId());
        if(peer == null) return;
        peer.setConnected(false);
        peerRepository.save(peer);
        LOG.info("peer on '{}' has disconnected", peer.getIp());
    }

    /**
     * The peer is added if not exists ({@link #addPeerIfNotPresent(PeerProfile)})
     * and it {@link NeoPeer#isConnected} is set to true.
     * @param profile
     */
    public void peerConnected(PeerProfile profile){
        addPeerIfNotPresent(profile);

        //TODO bad: is done in the method before if the peer doesnt exists before
        NeoPeer peer = peerRepository.findByPeerId(profile.getPeerId());
        peer.setConnected(true);
        peerRepository.save(peer);
        LOG.info("peer on '{}' has connected", peer.getIp());
    }

    boolean firstConnect = true;

    /**
     * If a new SuperPeer is requesting,
     * we close the client and remove all processes which are executed on the super peer.
     * Then we make a new connection to the new super peer and get all peers again.
     * If new requesting super peer and the old super peer are equal (ids are equal) then we do nothing,
     * as the client will indefinitely try to connect to the super peer (default should be something around 3 seconds).
     *
     * @param request
     */
    //TODO this method must be implemented in a thread safe way, so that all other operations not fail (e.g. client is null)
    public void superPeerIsRequesting(SuperPeerRequest request){

        //TODO workaround since the run method is not called but we need a clean repo at startup
        if(firstConnect){
            peerRepository.deleteAll();
            processRepository.deleteAll();
            firstConnect = false;
        }

        if(!checkSuperPeerArgs(request)) {
            LOG.error("invalid peer profile for super peer");
            return;
        }

        if(!superPeerHasChanged(request.profile))
            return;

        removeProcessesFromSuperPeer();

        if(client != null) client.close();
        currentSuperPeer = request.profile;
        client = null;
        connect(request);

        LOG.info("monitoring new super peer on '{}'", currentSuperPeer.getIp());

        client.getRegisteredPeers().forEach(p -> addPeerIfNotPresent(p));
        registerListeners();
    }

    private void removeProcessesFromSuperPeer(){
        Iterable<NeoProcess> processes = processRepository.findByPeerIsNull();
        processRepository.delete(processes);
    }

    private boolean superPeerHasChanged(PeerProfile newRequest){
        if(currentSuperPeer == null) return true;
        return !currentSuperPeer.getPeerId().equals(newRequest.getPeerId());
    }

    private void deleteOldPeerDataFor(NeoPeer peer){
        Iterable<NeoProcess> processes = processRepository.findByPeer(peer);
        processRepository.delete(processes);
        peerRepository.delete(peer);
    }

    private boolean runsOnPeer(IStateChangeMessage message){
        return message.getPeerId() != null && !message.getPeerId().isEmpty();
    }

    private boolean connectedToSuperPeer(){
        return client != null && client.isConnected();
    }

    private boolean checkPeerArg(PeerProfile profile){
        return profile.getIp() != null
                && !profile.getIp().isEmpty()
                && profile.getPeerId() != null
                && !profile.getPeerId().isEmpty();
    }

    private boolean checkSuperPeerArgs(SuperPeerRequest request){
        PeerProfile profile = request.profile;
        return profile != null
                && request.namespace != null && !request.namespace.isEmpty()
                && request.port != null && !request.port.isEmpty()
                && request.realm != null && !request.realm.isEmpty()
                && profile.isSuperPeer()
                && checkPeerArg(profile);
    }

    //FIXME we must exclude the shitty graphiti dependency from the client
    // it will require ui deps as well and is not available in maven
    private boolean connect(SuperPeerRequest request){
        client = new ProcessEngineClientBuilder()
                .withIp(request.profile.getIp())
                .withPort(request.port)
                .withRealmName(request.realm)
                .withNamespace(request.namespace)
                .withName(ProteusMonitorAgent.class.getSimpleName())
                .build();
        return client.connect();
    }

    private void registerListeners(){
        client.addConnectionListener(connectionListener);
        client.addStateChangeListener(message -> updateProcessStateFrom(message));
    }

    private boolean connect(SuperPeerConfig config){
        ProcessEngineClientBuilder builder = new ProcessEngineClientBuilder();
        client = builder
//                .withIp(config.ip)
                .withIp("127.0.0.1")
                .withPort(config.port)
                .withNamespace(config.namespace)
                .withRealmName(config.realm)
                .withName(ProteusMonitorAgent.class.getSimpleName())
                .build();
        return client.connect();
    }

    private static SuperPeerConfig getTestConfig(){
        SuperPeerConfig config = new SuperPeerConfig();
        config.ip = "localhost";
        config.port = "8081";
        config.namespace = "vicciWs";
        config.name = "vicciRealm";
        return config;
    }

    public static class SuperPeerConfig{
        public String name;
        public String ip;
        public String port;
        public String namespace;
        public String realm;
    }
}
