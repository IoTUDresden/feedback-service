package de.tud.feedback.plugin;

import de.tud.feedback.api.ContextUpdater;
import de.tud.feedback.api.MonitorAgent;
import de.tud.feedback.api.annotation.LogInvocation;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

public class OpenHabMonitorAgent extends AbstractWebSocketHandler implements MonitorAgent {

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
        new StandardWebSocketClient()
                .doHandshake(this, "ws://{host}:{port}/rest/items/?Accept=application/json", host, port);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        super.handleBinaryMessage(session, message);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
    }

}
