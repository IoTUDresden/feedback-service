package de.tud.feedback.plugin;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.CypherExecutor;
import de.tud.feedback.annotation.LogInvocation;
import de.tud.feedback.annotation.LogTimeSeries;
import de.tud.feedback.domain.Context;
import de.tud.feedback.event.SymptomDetectedEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;

import static de.tud.feedback.Utils.params;
import static java.util.stream.Collectors.toMap;

public class DogOntContextUpdater implements ContextUpdater {

    private final String stateNodePrefix;

    private final CypherExecutor executor;

    private Context context;

    private Map<String, Integer> stateValueMapping;

    private ApplicationEventPublisher publisher;

    public DogOntContextUpdater(CypherExecutor executor, String stateNodePrefix) {
        this.stateNodePrefix = stateNodePrefix;
        this.executor = executor;
    }

    @Override
    @LogInvocation
    @LogTimeSeries(context = "#this.context.name")
    public void update(String item, Object state) {
        final String stateName = stateNodePrefix + item;

        if (stateValueMapping == null) {
            stateValueMapping = resolveStateValueMapping();
        }

        if (stateValueMapping.containsKey(stateName)) {
            executor.execute(
                    "MATCH (v) " +
                    "WHERE ID(v) = {id} " +
                    "SET v.realStateValue = {value} " +
                    "RETURN v",

                    params().put("id", stateValueMapping.get(stateName))
                            .put("value", state)
                            .build());

            publisher.publishEvent(SymptomDetectedEvent.on(context));
        }
    }

    private Map<String, Integer> resolveStateValueMapping() {
        return executor.execute(
                    "MATCH (thing)-[:within]->(import:ContextImport) " +
                    "MATCH (import)-[:for]->(context:Context) " +
                    "MATCH (thing)-[:hasState]->(state) " +
                    "MATCH (state)-[:hasStateValue]->(value) " +
                    "WHERE context.name = {contextName} " +
                    "RETURN state.name AS state, ID(value) AS valueId",

                    params().put("contextName", context.getName())
                            .build())

                .stream()
                .collect(toMap(
                        e -> (String) e.get("state"),
                        e -> (Integer) e.get("valueId")));
    }

    @Override
    public void workWith(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

}
