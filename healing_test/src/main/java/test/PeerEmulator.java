package test;

import de.tud.feedback.domain.Command;
import eu.vicci.process.distribution.logging.DistributionCommand;
import eu.vicci.process.distribution.logging.LogEntry;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import test.jms.SimpleProducer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.eclipse.jetty.http.HttpParser.LOG;

/**
 * Created by Stefan on 16.06.2016.
 */
@Component
public class PeerEmulator implements CommandLineRunner,MessageListener {

    @Autowired
    private SimpleProducer producer;

    private final BlockingQueue<DistributionCommand> queue = new LinkedBlockingQueue<DistributionCommand>();

    @Override
    @JmsListener(destination = "server.messages")
    public void onMessage(Message message) {
        LOG.debug("Rec Message");
        if (message instanceof ObjectMessage)
        {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                if (objectMessage.getObject() instanceof DistributionCommand) {
                    DistributionCommand distributionCommand = (DistributionCommand) objectMessage.getObject();
                    LOG.debug("Consumer Test Received Command <" + distributionCommand + ">");
                    try {
                        queue.put(distributionCommand);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

        //entries.add(message);
    }

    private Peer peer1 = new Peer("Peer_1", "98c9ca7f-a5a2-40f0-ae88-c25a8e7ebad1", "192.168.1.63");
    private Peer peer2 = new Peer("Peer_2", "1124", "192.168.1.69");
    private Process process1 = new Process("SubProcess1", "_fqBe0BhIEeaIh_K9nEUPRA");

    private String metrics = "BatteryLevel : 0.39999992";
    private String metrics2 = "BatteryLevel : 0.9";


    public void handleCommand(DistributionCommand command) {
        System.out.println("Handle Command");
        if (command.getCommandName().equalsIgnoreCase("START")) {
            System.out.println("Got Peer Switch Command");
            //Workflow (mit jeweiligen Monitor update):
            //Peer 1 sendet Process Terminierung -> Prozess aus Peer 1 entfernen
            //LogEntry peer1Termination = createLogEntry(peer1, process1, "undeployed", "WAMPMESSAGE");
            //producer.send(peer1Termination);
            //Peer 2 sendet Metrik -> Metrik für Peer 2 updaten
            LogEntry peer2deploy= createLogEntry(peer2, process1, "active", "WAMPMESSAGE");

            producer.send(peer2deploy);
            LogEntry entry = createLogEntry(peer2, process1, metrics2, "METRICS");
            producer.send(entry);
            //Peer 2 sendet Prozess Empfang -> Prozess zu Peer 2 hinzufügen
            LogEntry peer2activation = createLogEntry(peer2, process1, "executing", "WAMPMESSAGE");

            producer.send(peer2activation);
            try {
                System.out.println("Sleep");
                Thread.sleep(5000);
                System.out.println("Sleep ende");
                LogEntry peer2finished = createLogEntry(peer2, process1, "executed", "WAMPMESSAGE");

                producer.send(peer2finished);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            }
    }

    private LogEntry createLogEntry(Peer peer, Process process, String message, String messageType) {
        LogEntry entry = new LogEntry();
        entry.setMessage(message);
        long unixTime = System.currentTimeMillis() / 1000L;
        entry.setTimestamp(String.valueOf(unixTime));
        entry.setProcessInstanceId(process.getInstanceId());
        entry.setMessageType(messageType);
        entry.setProcessName(process.getName());
        entry.setProcessStepId(process.getId());
        entry.setClientInstanceId(peer.getPeerId());
        entry.setClientName(peer.getPeerName());
        System.out.println("Sending: " + entry);
        return entry;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                DistributionCommand command = queue.take();
                handleCommand(command);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
