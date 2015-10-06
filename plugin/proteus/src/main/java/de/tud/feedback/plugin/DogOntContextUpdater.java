package de.tud.feedback.plugin;

import de.tud.feedback.api.ContextReference;
import de.tud.feedback.api.ContextUpdater;
import de.tud.feedback.api.CypherExecutor;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static de.tud.feedback.Utils.params;

public class DogOntContextUpdater implements ContextUpdater {

    private CypherExecutor executor;

    private ContextReference ref;

    private Map<String, Long> context = newHashMap();

    public DogOntContextUpdater(CypherExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void update(String item, Object state) {
        if (context.isEmpty())
            buildContext();

        if (context.containsKey(item)) {

        }
    }

    private void buildContext() {
        executor.execute(
                        "MATCH (c:Context { name: {name} })-[*1..5]-(i:Proteus { namespace: {namespace} }) " +
                        "RETURN i.name AS name, ID(i) AS id",

                params().put("name", ref.getName())
                        .put("namespace", ref.getItemNamespace())
                        .build())

                .forEach(this::putIntoContext);
    }

    private void putIntoContext(Map<String, Object> row) {

        // FIXME alles Scheiße
        // Instanz in Kontext erkennen:
        //
        //      (:Context { name: {name} })<-[:for]-(:ContextImport)<-[:within]-(item)-[:hasState]->(state)-[:hasStateValue]->(value)
        //
        // => Namespacing kann wieder raus
        // => ContextReference ist obsolete

    }

    @Override
    public void operateOn(ContextReference context) {
        ref = context;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
