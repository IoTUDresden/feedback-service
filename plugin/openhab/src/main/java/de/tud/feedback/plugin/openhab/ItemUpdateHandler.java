package de.tud.feedback.plugin.openhab;

import de.tud.feedback.ContextUpdater;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class ItemUpdateHandler {

    private final Map<String, String> cache = newHashMap();

    private final Double numberStateChangeDelta;

    private ContextUpdater updater;

    public ItemUpdateHandler(Double delta) {
        this.numberStateChangeDelta = delta;
    }

    public void setUpdater(ContextUpdater updater) {
        this.updater = updater;
    }

    public void handle(OpenHabItem item) {
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

    private boolean isSignificantChange(String currentState, String cachedState) {
        try {
            return Math.abs(Double.valueOf(currentState) - Double.valueOf(cachedState)) > numberStateChangeDelta;
        } catch (NumberFormatException exception) {
            // changes on strings are always significant
            return true;
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

}
