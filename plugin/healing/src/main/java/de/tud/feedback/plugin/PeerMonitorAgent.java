package de.tud.feedback.plugin;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.loop.MonitorAgent;
import de.tud.feedback.plugin.healing.StateMessageReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Stefan on 09.05.2016.
 */
public class PeerMonitorAgent implements MonitorAgent{
    private ContextUpdater updater;

    private StateMessageReceiver receiver;

    private static final Logger LOG = LoggerFactory.getLogger(PeerMonitorAgent.class);

    void PeerMonitorAgent(StateMessageReceiver receiver){
        this.receiver = receiver;
    }


    @Override
    public void workWith(ContextUpdater updater) {
        this.updater = updater;
    }

    @Override
    public void run() {

    }

    private void processStateMessage(){

    }
}
