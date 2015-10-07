package de.tud.feedback.api;

import java.util.Collection;
import java.util.Map;

public interface CypherExecutor {

    Collection<Map<String, Object>> execute(String cypherQuery, Map<String, ?> params);

}
