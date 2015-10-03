package de.tud.feedback.api;

import java.util.Map;

public interface CypherExecutor {

    void execute(String cypherQuery, Map<String, ?> params);

}
