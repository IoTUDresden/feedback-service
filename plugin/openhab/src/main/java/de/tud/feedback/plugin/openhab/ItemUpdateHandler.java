package de.tud.feedback.plugin.openhab;

import de.tud.feedback.ContextUpdater;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Math.abs;

public class ItemUpdateHandler {

    private final Map<String, OpenHabItem> cache = newHashMap();

    private final Map<String, Double> maximum = newHashMap();

    private final Map<String, Double> normalized = newHashMap();

    private final Double delta;

    private ContextUpdater updater;

    public ItemUpdateHandler(Double delta) {
        this.delta = delta;
    }

    public void setUpdater(ContextUpdater updater) {
        this.updater = updater;
    }

    public void handle(OpenHabItem item) {
        if (!cacheContains(item) || containsSignificantStateChange(item))
            update(item);

        cache(item);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean containsSignificantStateChange(OpenHabItem current) {
        if (current.getState().equals(cached(current).getState()))
            return false;

        else if (!hasNumberState(current))
            return true;

        else
            return hasSignificantDifference(current.getState(), current.getLink());
    }

    private boolean hasSignificantDifference(String state, String item) {
        double number = Double.valueOf(state);
        double last = normalized.getOrDefault(item, Double.MIN_VALUE);
        double max = maximum.getOrDefault(item, Double.MIN_VALUE);

        if (max < number) {
            maximum.put(item, number);
            max = number;
        }

        double current = (number / max);
        normalized.put(item, current);

        //noinspection SimplifiableIfStatement
        if (current == last && last == 1.0)
            return true;
        else
            return abs(current - last) > delta;
    }

    private boolean hasNumberState(OpenHabItem item) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Double.valueOf(item.getState());
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private OpenHabItem cached(OpenHabItem item) {
        return cache.get(item.getLink());
    }

    private void update(OpenHabItem item) {
        updater.update(item.getName(), item.getState());
    }

    private boolean cacheContains(OpenHabItem item) {
        return cache.containsKey(item.getLink());
    }

    private OpenHabItem cache(OpenHabItem item) {
        return cache.put(item.getLink(), item);
    }

}
