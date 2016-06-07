package de.tud.feedback.plugin;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.loop.MonitorAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Monitors LogEntry on changed peer metrics, e.g. changed workloads.
 * <p>
 * Created by Stefan on 09.05.2016.
 */
public class PeerMetricsMonitorAgent implements MonitorAgent {
    private ContextUpdater updater;

    private final BlockingQueue<LogEntry> queue = new LinkedBlockingQueue<LogEntry>();


    private static final Logger LOG = LoggerFactory.getLogger(PeerMetricsMonitorAgent.class);

    @Override
    public void workWith(ContextUpdater updater) {
        this.updater = updater;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                LogEntry logEntry = queue.take();
                if (logEntry.getMessageType().equalsIgnoreCase("WAMPMESSAGE"))
                {
                    updater.update(logEntry.getProcessName(), logEntry.getMessage());
                    LOG.debug("Updating Peer Metrics: "+logEntry.getMessage());
                }
                if (logEntry.getMessageType().equalsIgnoreCase("PINGRESPONSE"))
                    updater.update(logEntry.getClientName(),logEntry.getTimestamp());
            //TODO: aktuell eigentlich der ProcessMonitorAgent
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
