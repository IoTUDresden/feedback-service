package de.tud.feedback.api.context;

import java.util.Map;

public interface CypherOperations {

    Iterable<Map<String,Object>> execute(String cypherQuery, Map<String, ?> params);

}
