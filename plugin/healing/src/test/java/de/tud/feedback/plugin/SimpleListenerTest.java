package de.tud.feedback.plugin;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Log4JLogger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TestApplication.class)
public class SimpleListenerTest {

    @Autowired
    private JmsTemplate producer;

    @Autowired
    private PeerMetricsMonitorAgent monitorAgent;

    private List<LogEntry> entries;

    @Test
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
                producer.convertAndSend("client.messages", entry);
                Thread.sleep(1000);

            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}