package de.tud.feedback.plugin;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tud.feedback.api.ContextUpdater;
import de.tud.feedback.api.MonitorAgent;
import de.tud.feedback.api.annotation.LogInvocation;
import de.tud.feedback.plugin.openhab.OpenHabItem;
import de.tud.feedback.plugin.openhab.OpenHabMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class OpenHabMonitorAgent extends TextWebSocketHandler implements MonitorAgent {

    private static final Logger LOG = LoggerFactory.getLogger(OpenHabMonitorAgent.class);

    private static final String ADDRESS = "ws://{host}:{port}/rest/items/?" +
            "Accept=application/json&X-Atmosphere-Transport=websocket&X-Atmosphere-Framework=2.2.1";

    private final ObjectMapper mapper = new ObjectMapper();

    private final WebSocketConnectionManager manager;

    private final Map<String, String> cache = newHashMap();

    private Double numberStateChangeDelta = 0.01;

    private ContextUpdater updater;

    private String messageBuffer = "";

    public OpenHabMonitorAgent(String host, Integer port) {
        manager = new WebSocketConnectionManager(new StandardWebSocketClient(), this, ADDRESS, host, port);
    }

    public void setNumberStateChangeDelta(Double delta) {
        numberStateChangeDelta = delta;
    }

    @Override
    @LogInvocation
    public void start(ContextUpdater updater) {
        this.updater = updater;
        manager.start();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            messageBuffer += message.getPayload();
            mapper.readValue(messageBuffer, OpenHabMessage.class).getItems().stream().forEach(this::handleItem);
            messageBuffer = "";

        } catch (JsonMappingException exception) {
            // no need to handle partial message explicitly

        } catch (Exception exception) {
            LOG.warn("Message dumped due to " + exception.getMessage());
            messageBuffer = "";
        }
    }

    private void handleNumberItem(OpenHabItem item) {
        if (!cacheContains(item)) {
            cache(item);
            update(item);

        } else {
            final String cachedState = cached(item);
            final String currentState = item.getState();

            if (!cachedState.equals(currentState) && isSignificantChange(currentState, cachedState)) {
                cache(item);
                update(item);
            }
        }
    }

    private void handleItem(OpenHabItem item) {
        switch (item.getType()) {
            case "NumberItem": handleNumberItem(item); break;

            // other items will be ignored
            case "ColorItem":
            case "ContactItem":
            case "DateTimeItem":
            case "DimmerItem":
            case "LocationItem":
            case "RollershutterItem":
            case "StringItem":
            case "SwitchItem":
            default:
                break;
        }
    }

    private String cached(OpenHabItem item) {
        return cache.get(item.getLink());
    }

    private void update(OpenHabItem item) {
        updater.update(item.getName(), item.getState());
    }

    private boolean cacheContains(OpenHabItem item) {
        return cache.containsKey(item.getLink());
    }

    private String cache(OpenHabItem item) {
        return cache.put(item.getLink(), item.getState());
    }

    private boolean isSignificantChange(String currentState, String cachedState) {
        return Math.abs(Double.valueOf(currentState) - Double.valueOf(cachedState)) > numberStateChangeDelta;
    }

}
