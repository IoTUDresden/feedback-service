package de.tud.feedback.plugin.openhab;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Math.abs;

public class RelevancyFilter {

    private final Map<String, OpenHabItem> cache = newHashMap();

    private final Map<String, Double> maximum = newHashMap();

    private final Map<String, Double> normalized = newHashMap();

    private final Double delta;

    public RelevancyFilter(Double delta) {
        this.delta = delta;
    }

    public boolean isRelevant(OpenHabItem item) {
        boolean isRelevant = (!cacheContains(item) || containsSignificantStateChange(item));
        updateCached(item);
        return isRelevant;
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

    private boolean cacheContains(OpenHabItem item) {
        return cache.containsKey(item.getLink());
    }

    private OpenHabItem updateCached(OpenHabItem item) {
        return cache.put(item.getLink(), item);
    }

}
