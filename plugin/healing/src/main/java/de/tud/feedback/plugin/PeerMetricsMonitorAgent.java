package de.tud.feedback.plugin;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.loop.MonitorAgent;
import eu.vicci.process.distribution.logging.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.google.common.base.Preconditions.checkNotNull;

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
                String messageType = checkNotNull(logEntry.getMessageType());

                if (messageType.equalsIgnoreCase("WAMPMESSAGE")) {
                    updater.update(logEntry.getProcessName(), logEntry.getMessage());
                    LOG.debug("Updating Peer Metrics: " + logEntry.getMessage());
                } else if (messageType.equalsIgnoreCase("PINGRESPONSE"))
                    updater.update(logEntry.getClientName(), logEntry.getTimestamp());
                else if (messageType.equalsIgnoreCase("METRICS")) {
                    String[] metrics = logEntry.getMessage().split(":");
                    String name = metrics[0].trim();
                    String state = metrics[1].trim();
                    LOG.debug("Update Peer Metric: "+metrics.toString());
                    updater.update(name,state);
                }
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

    /**
     * Updates Peer Timestamp everythime a Message arrives.
     * @param entry
     */
    private void updateTimestamp(LogEntry entry){
        String peerName = checkNotNull(entry.getClientName());
        String timestamp = checkNotNull(entry.getTimestamp());
        if (!peerName.isEmpty())
            updater.update(peerName, timestamp);
    }
    private void updateResponseTime(LogEntry entry){

    }
}
