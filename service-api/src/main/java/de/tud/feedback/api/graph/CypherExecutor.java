package de.tud.feedback.api.graph;

import java.util.Map;

public interface CypherExecutor {

    void execute(String cypherQuery, Map<String, ?> params);

}
