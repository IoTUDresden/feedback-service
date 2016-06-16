package test.jms;

import de.tud.feedback.domain.Command;
import eu.vicci.process.distribution.logging.LogEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Queue;
import java.util.List;

import static org.eclipse.jetty.http.HttpParser.LOG;

@Component
public class SimpleListenerTest {

    private List<LogEntry> entries;

    @JmsListener(destination = "client.messages")
    public void receiveMessage(Command message) {

        LOG.info("Consumer Test Received Command <" + message + ">");
        //entries.add(message);
    }

}