package de.tud.feedback.graph;

import de.tud.feedback.api.CypherExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SimpleCypherExecutor implements CypherExecutor {

    private final Neo4jOperations operations;

    @Autowired
    public SimpleCypherExecutor(Neo4jOperations operations) {
        this.operations = operations;
    }

    @Override
    public void execute(String cypherQuery, Map<String, ?> params) {
        operations.query(cypherQuery, params);
    }

}
