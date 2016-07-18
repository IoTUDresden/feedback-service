package de.tud.feedback.plugin;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.loop.MonitorAgent;
import eu.vicci.process.distribution.logging.DistributionCommand;
import eu.vicci.process.distribution.logging.LogEntry;
import org.apache.commons.logging.Log;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import static org.joda.time.DateTime.now;


import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Monitors LogEntry on changed peer metrics, e.g. changed workloads.
 * <p>
 * Created by Stefan on 09.05.2016.
 */
public class PeerMetricsMonitorAgent implements MonitorAgent, MessageListener {
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

                if (messageType.equalsIgnoreCase("WAMPMESSAGE") || messageType.equalsIgnoreCase("REMOTESTEPRESPONSE") ) {
                    updateProcessState(logEntry);
                    updateTimestamp(logEntry);
                } else if (messageType.equalsIgnoreCase("PINGRESPONSE")) {
                    updateTimestamp(logEntry);
                } else if (messageType.equalsIgnoreCase("PING")) {
                    updateTimestamp(logEntry);
                } else if (messageType.equalsIgnoreCase("METRICS")) {
                    updateMetricState(logEntry);
                    updateTimestamp(logEntry);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateMetricState(LogEntry logEntry) {
        String[] metrics = logEntry.getMessage().split(":");
        String peerName = checkNotNull(logEntry.getClientName());
        String metricName = metrics[0].trim();
        String state = metrics[1].trim();
        LOG.debug("Update Peer Metric: " + metricName +" : "+ state);
        updater.update(peerName + metricName, state);
    }

    @JmsListener(destination = "client.messages")
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                if (objectMessage.getObject() instanceof LogEntry){
                    LogEntry logEntry = (LogEntry) objectMessage.getObject();
                    //LOG.info("Received <" + logEntry + ">");
                    try {
                        queue.put(logEntry);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates Peer Timestamp everythime a Message arrives.
     *
     * @param entry
     */
    private void updateTimestamp(LogEntry entry) {
        String peerName = checkNotNull(entry.getClientName());
        String timestamp = checkNotNull(entry.getTimestamp());
        DateTime time = now();
        if (!peerName.isEmpty())
            updater.update(peerName + "HeartbeatTime", time.toString());
        LOG.debug("Update Peer HeartbeatTime: " + time);

    }
    private void updateProcessState(LogEntry entry) {
        String processName = checkNotNull(entry.getProcessName());
        String processState = checkNotNull(entry.getMessage());
        if (!processName.isEmpty()){
            updater.update(processName+"Process",processState);
            LOG.debug("Updating ProcessState: " + entry.getMessage());
        }
    }
    //TODO: impol. resp., execu.
    private void updateResponseTime(LogEntry entry) {
        String peerName = checkNotNull(entry.getClientName());
        String timestamp = checkNotNull(entry.getTimestamp());
        if (!peerName.isEmpty())
            updater.update(peerName + "ResponseTime", timestamp);
    }


    private void updateExecutionTime(LogEntry entry) {
        String peerName = checkNotNull(entry.getClientName());
        String timestamp = checkNotNull(entry.getTimestamp());
        if (!peerName.isEmpty())
            updater.update(peerName + "ResponseTime", timestamp);
    }
}
