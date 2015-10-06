package de.tud.feedback.plugin;

import de.tud.feedback.api.ContextReference;
import de.tud.feedback.api.ContextUpdater;
import de.tud.feedback.api.CypherExecutor;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static de.tud.feedback.Utils.params;
import static java.util.stream.Collectors.toSet;

public class DogOntContextUpdater implements ContextUpdater {

    private CypherExecutor executor;

    private ContextReference ref;

    private Collection<String> context;

    private final Collection<String> batch = newArrayList();

    public DogOntContextUpdater(CypherExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void update(String item, Object state) {
        if (context == null) {
            context = itemsWithinContext();
        }

        if (context.contains(item)) {

        }
    }

    private Collection<String> itemsWithinContext() {
        return newArrayList(executor.execute(
                        "MATCH (c:Context { name: {name} })-[*1..5]-(i:Proteus { namespace: {namespace} }) " +
                        "RETURN i.name AS name",

                params().put("name", ref.getName())
                        .put("namespace", ref.getItemNamespace())
                        .build()))

                .stream()
                .map(row -> row.get("name"))
                .map(Object::toString)
                .collect(toSet());
    }

    @Override
    public ContextUpdater on(ContextReference context) {
        ref = context;
        return this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
