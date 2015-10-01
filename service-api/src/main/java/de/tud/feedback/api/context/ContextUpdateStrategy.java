package de.tud.feedback.api.context;

import de.tud.feedback.api.graph.CypherExecutor;

public interface ContextUpdateStrategy {

    void updateContextWith(CypherExecutor executor);

}
