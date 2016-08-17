package de.tud.feedback.plugin;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Math.abs;

/**
 * Created by Stefan on 17.08.2016.
 */
public class MetricSignificanceFilter {
    private final Map<String, MetricItem> cache = newHashMap();

    private final Map<String, Double> maximum = newHashMap();

    private final Map<String, Double> normalized = newHashMap();

    private final Double delta;

    public MetricSignificanceFilter(Double delta) {
        this.delta = delta;
    }

    public boolean containsSignificantChange(MetricItem item) {
        boolean isRelevant = (!cacheContains(item) || containsSignificantStateChange(item));
        updateCached(item);
        return isRelevant;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean containsSignificantStateChange(MetricItem current) {
        if (current.getState().equals(cached(current).getState()))
            return false;

        else if (!hasNumberState(current))
            return true;

        else
            return hasSignificantDifference(current.getState(), current.getName());
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

        if (current == last && last == 1.0) return true;
        else return abs(current - last) > delta;
    }

    private boolean hasNumberState(MetricItem item) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Double.valueOf(item.getState());
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private MetricItem cached(MetricItem item) {
        return cache.get(item.getName());
    }

    private boolean cacheContains(MetricItem item) {
        return cache.containsKey(item.getName());
    }

    private MetricItem updateCached(MetricItem item) {
        return cache.put(item.getName(), item);
    }
}
