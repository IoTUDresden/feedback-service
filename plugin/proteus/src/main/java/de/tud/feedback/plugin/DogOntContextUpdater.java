package de.tud.feedback.plugin;

import de.tud.feedback.api.ContextUpdater;
import de.tud.feedback.api.CypherExecutor;
import de.tud.feedback.api.NamedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static de.tud.feedback.Utils.params;
import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

public class DogOntContextUpdater implements ContextUpdater {

    private static final Logger LOG = LoggerFactory.getLogger(DogOntContextUpdater.class);

    private CypherExecutor executor;

    private String contextName;

    private Map<String, Integer> context;

    public DogOntContextUpdater(CypherExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void update(String item, Object state) {
        if (context == null)
            context = createContext();

        if (context.containsKey(item)) {
            executor.execute(
                    "MATCH (v) " +
                    "WHERE ID(v) = {id} " +
                    "SET v.realStateValue = {value} " +
                    "RETURN v",

                    params().put("id", context.get(item))
                            .put("value", state)
                            .build());

            LOG.debug(format("SET %s = %s", item, state));
        }
    }

    private Map<String, Integer> createContext() {
        return executor.execute(
                "MATCH (c:Context)<-[:for]-(:ContextImport)<-[:within]-(i)-[:hasState]->(s)-[:hasStateValue]->(v) " +
                "WHERE c.name = {contextName} " +
                "RETURN i.name AS item, ID(v) AS valueId",

                params().put("contextName", contextName)
                        .build())

                .stream()
                .collect(toMap(
                        e -> (String) e.get("item"),
                        e -> (Integer) e.get("valueId")));
    }

    @Override
    public void workWith(NamedNode context) {
        this.contextName = context.name();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
