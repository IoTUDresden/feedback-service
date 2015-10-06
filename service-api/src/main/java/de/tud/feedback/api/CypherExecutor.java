package de.tud.feedback.api;

import java.util.Map;

public interface CypherExecutor {

    Iterable<Map<String, Object>> execute(String cypherQuery, Map<String, ?> params);

}
