package de.tud.feedback.plugin;

import de.tud.feedback.api.ContextUpdater;
import de.tud.feedback.api.CypherExecutor;

public class DogOntContextUpdater implements ContextUpdater {

    private CypherExecutor executor;

    public DogOntContextUpdater(CypherExecutor executor) {
        this.executor = executor;
        // TODO fetch related item IDs
    }

    @Override
    public void update(String itemId, Object state) {
        // TODO
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
