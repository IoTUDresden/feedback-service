package de.tud.feedback.plugin;

import de.tud.feedback.api.ContextUpdater;
import de.tud.feedback.api.CypherExecutor;

public class DogOntContextUpdater implements ContextUpdater {

    private final CypherExecutor executor;

    public DogOntContextUpdater(CypherExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void updateContext(Long contextId, String itemId, Object state) {
        // TODO
    }

}
