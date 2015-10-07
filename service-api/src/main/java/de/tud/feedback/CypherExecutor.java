package de.tud.feedback;

import java.util.Collection;
import java.util.Map;

public interface CypherExecutor {

    Collection<Map<String, Object>> execute(String cypherQuery, Map<String, ?> params);

}
