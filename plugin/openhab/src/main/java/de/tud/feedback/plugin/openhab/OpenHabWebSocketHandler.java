package de.tud.feedback.plugin.openhab;

import de.tud.feedback.api.context.RealityChangeHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

public class OpenHabWebSocketHandler extends AbstractWebSocketHandler {

    private final RealityChangeHandler consumer;

    public OpenHabWebSocketHandler(RealityChangeHandler consumer) {
        this.consumer = consumer;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // TODO
        consumer.handleChangeOn("anyItem123", 42);
    }

}
