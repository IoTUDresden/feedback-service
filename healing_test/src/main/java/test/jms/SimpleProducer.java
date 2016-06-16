package test.jms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.vicci.process.distribution.logging.LogEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;
import java.io.File;
import java.io.IOException;

/**
 * Created by Stefan on 16.06.2016.
 */
@Component
public class SimpleProducer implements CommandLineRunner{

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private Queue queue;

    @Override
    public void run(String... args) throws Exception {
        sendSimpleMessageTree();
        System.out.println("Message was sent to the Queue");
    }

    public void send(Object msg) {
        this.jmsMessagingTemplate.convertAndSend(this.queue, msg);
    }
    public void sendSimpleMessageTree() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode tree = mapper.readTree(new File("C:\\\\Temp\\log.json"));
            for (JsonNode leaf: tree
                    ) {
                LogEntry entry = new LogEntry();
                entry.setClientInstanceId(leaf.get("clientInstanceId").textValue());
                entry.setMessage(leaf.get("message").textValue());
                entry.setTimestamp(leaf.get("timestamp").textValue());
                entry.setProcessInstanceId(leaf.get("processInstanceId").textValue());
                entry.setMessageType(leaf.get("messageType").textValue());
                entry.setProcessName(leaf.get("processName").textValue());
                entry.setProcessStepId(leaf.get("processStepId").textValue());
                entry.setClientInstanceId(leaf.get("clientInstanceId").textValue());
                entry.setClientName(leaf.get("clientName").textValue());
                System.out.println("Sending: "+entry);
                send(entry);
                Thread.sleep(1000);

            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}