package de.tud.feedback.plugin;

import de.tud.feedback.api.ContextUpdater;
import de.tud.feedback.api.MonitorAgent;
import de.tud.feedback.api.annotation.LogInvocation;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class OpenHabMonitorAgent extends TextWebSocketHandler implements MonitorAgent {

    private final String host;

    private final Integer port;

    private ContextUpdater updater;

    public OpenHabMonitorAgent(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    @Override
    @LogInvocation
    public void start(ContextUpdater updater) {
        this.updater = updater;

        final StandardWebSocketClient client = new StandardWebSocketClient();
        final WebSocketConnectionManager manager = new WebSocketConnectionManager(client, this,
                "ws://{host}:{port}/rest/items/?Accept=application/json&" +
                        "X-Atmosphere-Transport=websocket&" +
                        "X-Atmosphere-Framework=2.0&" +
                        "X-atmo-protocol=true&" +
                        "X-Atmosphere-tracking-id=42&" +
                        "X-Cache-Date=0",
                host, port);

        manager.start();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
    }

}
