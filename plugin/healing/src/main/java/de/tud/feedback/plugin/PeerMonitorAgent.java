package de.tud.feedback.plugin;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.loop.MonitorAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Stefan on 03.06.2016.
 */
public class PeerMonitorAgent implements MonitorAgent {
    private ContextUpdater updater;

    private final BlockingQueue<LogEntry> queue = new LinkedBlockingQueue<LogEntry>();


    private static final Logger LOG = LoggerFactory.getLogger(PeerMonitorAgent.class);
    @Override
    public void workWith(ContextUpdater updater) {

    }

    @Override
    public void run() {

    }
    @JmsListener(destination = "client.messages")
    public void receiveMessage(LogEntry message) {

        LOG.info("Received <" + message + ">");
        try {
            queue.put(message);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
