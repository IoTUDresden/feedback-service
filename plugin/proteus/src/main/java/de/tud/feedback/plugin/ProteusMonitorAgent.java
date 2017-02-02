package de.tud.feedback.plugin;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.plugin.domain.NeoPeer;
import de.tud.feedback.plugin.domain.NeoProcess;
import eu.vicci.process.client.ProcessEngineClientBuilder;
import eu.vicci.process.client.core.IProcessEngineClient;
import eu.vicci.process.distribution.core.PeerProfile;
import eu.vicci.process.model.util.messages.core.IStateChangeMessage;
import eu.vicci.process.model.util.messages.core.StateChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This agent monitors all interesting stuff from proteus (e.g. state changes, new peers)
 */
public class ProteusMonitorAgent implements ProcessMonitorAgent {
    private static final Logger LOG = LoggerFactory.getLogger(ProteusMonitorAgent.class);

    private IProcessEngineClient client;

    private NeoProcessRepository processRepository;
    private NeoPeerRepository peerRepository;

    private ProcessIdFormatter idFormatter;

    @Override
    public void workWith(ContextUpdater updater) {
        // not used as we dont use the current available dogont updater
    }

    @Override
    public void run() {
        //TODO only for testing, we connect direct to proteus
        //TODO maybe no heartbeat needed
        // (allready implemented in wamp - maybe we can handle disconnects there and publish the events on the server - or just poll)

        //TODO cleanup all repos after startup

        SuperPeerConfig config = getTestConfig();
        if(!connect(config))
            throw new RuntimeException("Cant connect to superpeer");

        client.getRegisteredPeers().forEach(p -> addPeerIfNotPresent(p));
        client.addStateChangeListener(stateChangeListener);

        //TODO how do we get, that the runtime is stopped, so that we can close the client?
    }

    private StateChangeListener stateChangeListener = new StateChangeListener() {
        @Override
        public void onMessage(IStateChangeMessage message) {
            updateProcessStateFrom(message);
        }
    };

    //TODO use transactions?
    private void updateProcessStateFrom(IStateChangeMessage message){
        String id = idFormatter.formatId(message.getPeerId(), message.getInstanceId());
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

    private void addPeerIfNotPresent(PeerProfile peerProfile){
        NeoPeer peer = new NeoPeer();
        peer.setName(peerProfile.getHostName());
        peer.setIp(peerProfile.getIp());
        peer.setSuperPeer(false);
        //TODO set the current time as init value
        peerRepository.save(peer);
    }

    private boolean runsOnPeer(IStateChangeMessage message){
        return message.getPeerId() != null && message.getPeerId().isEmpty();
    }

    private boolean connectedToSuperPeer(){
        return client != null && client.isConnected();
    }

    private boolean connect(SuperPeerConfig config){
        ProcessEngineClientBuilder builder = new ProcessEngineClientBuilder();
        client = builder
                .withIp(config.ip)
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
