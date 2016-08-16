package de.tud.feedback.plugin;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.CypherExecutor;
import de.tud.feedback.annotation.LogInvocation;
import de.tud.feedback.annotation.LogTimeSeries;
import de.tud.feedback.domain.Context;

import java.util.Map;
import java.util.function.Function;

import static de.tud.feedback.Utils.params;
import static java.util.stream.Collectors.toMap;

/**
 * Created by Stefan on 18.06.2016.
 */
public class PeerProcessContextUpdater implements ContextUpdater {
    private final Function<String, String> stateNameMapper;

    private final CypherExecutor executor;

    private Context context;

    private Map<String, Integer> stateValueMapping;

    private Listener listener;

    public PeerProcessContextUpdater(CypherExecutor executor, Function<String, String> stateNameMapper) {
        this.stateNameMapper = stateNameMapper;
        this.executor = executor;
    }

    @Override
    @LogInvocation
    @LogTimeSeries(context = "context.#{context.name}")
    public void update(String item, Object state) {

    }


    @Override
    public void workWith(Context context) {
        this.context = context;
    }

    @Override
    public void workWith(Listener listener) {
        this.listener = listener;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
