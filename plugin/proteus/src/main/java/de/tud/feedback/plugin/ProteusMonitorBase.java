package de.tud.feedback.plugin;


import de.tud.feedback.ContextUpdater;
import de.tud.feedback.plugin.events.ProteusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.tud.feedback.plugin.events.ProteusEvent.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class ProteusMonitorBase implements ProcessMonitorAgent {
    protected static final Logger LOG = LoggerFactory.getLogger(ProteusMonitorAgent.class);

    private BlockingQueue<ProteusEvent> events = new ArrayBlockingQueue<ProteusEvent>(1024,true);

    private boolean terminate = false;

    public ProteusMonitorBase() {
        //FIXME the monitor should be started by the feedback service and not at this point

        Thread t = new Thread(this);
        t.setDaemon(true);
        t.setName("Proteus Monitor");
        t.start();
    }

    @Override
    public void workWith(ContextUpdater updater) {
        // not used as we dont use the current available dogont updater
    }

    protected void addEvent(ProteusEvent event){
        try {
            events.put(event);
        } catch (InterruptedException e) {
            LOG.error("error while adding new event");
        }
    }

    Boolean isRunning = false;

    @Override
    public void run() {
        synchronized (isRunning){
            if(isRunning){
                LOG.error("already running");
                return;
            }
            isRunning = true;
        }

        initMonitor();

        while (!terminate) {
            try {
                handleEvent(events.take());
            } catch (InterruptedException e) {
                LOG.error("event handling interrupted",e);
            }
        }
    }

    /**
     * Stops the PROtEUS monitor
     */
    public void stop(){
        terminate = true;
        addEvent(new TerminateEvent());
    }

    /**
     * Called at the beginning once, before going in the loop
     */
    protected abstract void initMonitor();

    protected abstract void handleNewSuperPeer(NewSuperPeerEvent event);
    protected abstract void handlePeerDisconnected(PeerDisconnectedEvent event);
    protected abstract void handlePeerConnected(PeerConnectedEvent event);
    protected abstract void handleStateChange(StateChangeEvent event);

    private void handleEvent(ProteusEvent event){
        if(event instanceof NewSuperPeerEvent)
            handleNewSuperPeer((NewSuperPeerEvent)event);
        else if(event instanceof PeerConnectedEvent)
            handlePeerConnected((PeerConnectedEvent)event);
        else if(event instanceof PeerDisconnectedEvent)
            handlePeerDisconnected((PeerDisconnectedEvent)event);
        else if(event instanceof StateChangeEvent)
            handleStateChange((StateChangeEvent)event);
        else if(event instanceof TerminateEvent)
            LOG.info("shutting down");
        else
            LOG.error("cant handle event from type '{0}'", event == null ? "null" : event.getClass().getSimpleName());
    }

    private static class TerminateEvent extends ProteusEvent{}
}
