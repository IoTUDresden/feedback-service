package de.tud.feedback.graph;

import de.tud.feedback.CypherExecutor;

import java.util.Set;

public interface CollectingCypherExecutor extends CypherExecutor {

    Set<Long> createdNodes();

}
