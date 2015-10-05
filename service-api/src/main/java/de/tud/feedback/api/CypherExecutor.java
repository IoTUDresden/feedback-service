package de.tud.feedback.api;

import java.util.Map;
import java.util.Set;

public interface CypherExecutor {

    Iterable<Map<String, Object>> execute(String cypherQuery, Map<String, ?> params);

    Set<Long> createdNodes();

}
