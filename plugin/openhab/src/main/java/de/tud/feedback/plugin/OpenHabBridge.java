package de.tud.feedback.plugin;

import de.tud.feedback.api.context.RealityChangeHandler;
import de.tud.feedback.plugin.openhab.OpenHabWebSocketHandler;
import org.springframework.stereotype.Component;

@Component
public class OpenHabBridge {

    /*private final String host;

    private final int port;

    @Autowired
    public OpenHabBridge(@Value("${openHub.host}") String host, @Value("${openHub.port}") int port) {
        this.host = host;
        this.port = port;
    }*/

    public void connect(RealityChangeHandler updateConsumer) {
        OpenHabWebSocketHandler handler = new OpenHabWebSocketHandler(updateConsumer);
        // TODO
    }

}
