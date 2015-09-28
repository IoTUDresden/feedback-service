package de.tud.feedback.api.context;

import java.util.Map;

public interface CypherExecutor {

    void execute(String cypherQuery, Map<String, ?> params);

}
