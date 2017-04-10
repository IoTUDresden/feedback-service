package de.tud.feedback;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public abstract class Utils {

    private Utils() {}

    public static ImmutableMap.Builder<String, Object> params() {
        return ImmutableMap.<String, Object>builder();
    }

}
