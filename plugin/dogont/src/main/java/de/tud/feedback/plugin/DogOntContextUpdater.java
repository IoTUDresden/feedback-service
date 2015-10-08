package de.tud.feedback.plugin;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.CypherExecutor;
import de.tud.feedback.domain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static de.tud.feedback.Utils.params;
import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

public class DogOntContextUpdater implements ContextUpdater {

    private static final Logger LOG = LoggerFactory.getLogger(DogOntContextUpdater.class);

    private final String stateNodePrefix;

    private final CypherExecutor executor;

    private String contextName;

    private Map<String, Integer> stateValueMapping;

    public DogOntContextUpdater(CypherExecutor executor, String stateNodePrefix) {
        this.stateNodePrefix = stateNodePrefix;
        this.executor = executor;
    }

    @Override
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

            LOG.debug(format("SET %s = %s", item, state));
        }
    }

    private Map<String, Integer> resolveStateValueMapping() {
        return executor.execute(
                "MATCH (c:Context)<-[:for]-(:ContextImport)<-[:within]-(i)-[:hasState]->(s)-[:hasStateValue]->(v) " +
                "WHERE c.name = {contextName} " +
                "RETURN s.name AS state, ID(v) AS valueId",

                params().put("contextName", contextName)
                        .build())

                .stream()
                .collect(toMap(
                        e -> (String) e.get("state"),
                        e -> (Integer) e.get("valueId")));
    }

    @Override
    public void workWith(Context context) {
        this.contextName = context.getName();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
