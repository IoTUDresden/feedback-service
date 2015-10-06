package de.tud.feedback.graph;

import de.tud.feedback.api.CypherExecutor;

import java.util.Set;

public interface CollectingCypherExecutor extends CypherExecutor {

    Set<Long> createdNodes();

}
