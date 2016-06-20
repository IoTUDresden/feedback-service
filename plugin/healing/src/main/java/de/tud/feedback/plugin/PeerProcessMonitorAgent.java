package de.tud.feedback.plugin;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.loop.MonitorAgent;
import eu.vicci.process.distribution.logging.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Stefan on 18.06.2016.
 */
public class PeerProcessMonitorAgent implements MonitorAgent, MessageListener {
    private ContextUpdater updater;

    private final BlockingQueue<LogEntry> queue = new LinkedBlockingQueue<LogEntry>();


    private static final Logger LOG = LoggerFactory.getLogger(PeerProcessMonitorAgent.class);

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
                String payload = checkNotNull(logEntry.getMessage());
                if (messageType.equalsIgnoreCase("WAMPMESSAGE") && payload.equalsIgnoreCase("active")) {
                    LOG.debug("Updating Process2Peer Mapping: " + logEntry.getMessage());
                    updatePeersProcess(logEntry);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    @JmsListener(destination = "client.messages")
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                if (objectMessage.getObject() instanceof LogEntry){
                    LogEntry logEntry = (LogEntry) objectMessage.getObject();
                    String messageType = checkNotNull(logEntry.getMessageType());
                    LOG.info("Updateing Peer to Process <" + logEntry + ">");
                    try {
                        if (messageType.equalsIgnoreCase("WAMPMESSAGE")) {
                            queue.put(logEntry);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
    private void updatePeersProcess(LogEntry entry){
        String peerName = checkNotNull(entry.getClientName());
        String processName = checkNotNull(entry.getProcessName());
        if (!peerName.isEmpty() && !processName.isEmpty()){
            updater.update(peerName,processName);
        }
    }
}
