package de.tud.feedback.plugin;

import de.tud.feedback.api.ContextUpdater;
import de.tud.feedback.api.MonitorAgent;
import de.tud.feedback.api.annotation.LogInvocation;
import de.tud.feedback.plugin.openhab.OpenHabService;
import de.tud.feedback.plugin.openhab.domain.OpenHabItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class OpenHabMonitorAgent implements MonitorAgent {

    private static final Logger LOG = LoggerFactory.getLogger(OpenHabMonitorAgent.class);

    private final Map<String, String> cache = newHashMap();

    private final OpenHabService service;

    private Double numberStateChangeDelta = 0.01;

    private Integer pollingSeconds = 2;

    private ContextUpdater updater;

    public OpenHabMonitorAgent(OpenHabService service) {
        this.service = service;
    }

    @Override
    public void use(ContextUpdater updater) {
        this.updater = updater;
    }

    @Override
    @LogInvocation
    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        while (true) {
            processOpenHabItems();
            postponeNextRequest();
        }
    }

    private void processOpenHabItems() {
        service.getAllItems().getItems().stream().forEach(this::handleItem);
    }

    private void postponeNextRequest() {
        try {
            Thread.sleep(1000L * pollingSeconds);
        } catch (InterruptedException exception) {
            LOG.info(exception.getMessage());
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

    public void setNumberStateChangeDelta(Double delta) {
        numberStateChangeDelta = delta;
    }

    public void setPollingSeconds(Integer seconds) {
        this.pollingSeconds = seconds;
    }

}
