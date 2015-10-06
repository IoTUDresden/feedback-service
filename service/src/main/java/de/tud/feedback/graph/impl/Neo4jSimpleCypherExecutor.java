package de.tud.feedback.graph.impl;

import de.tud.feedback.graph.SimpleCypherExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Scope("prototype")
public class Neo4jSimpleCypherExecutor implements SimpleCypherExecutor {

    private final Neo4jOperations operations;

    @Autowired
    public Neo4jSimpleCypherExecutor(Neo4jOperations operations) {
        this.operations = operations;
    }

    @Override
    public Iterable<Map<String, Object>> execute(String cypherQuery, Map<String, ?> params) {
        return operations.query(cypherQuery, params).queryResults();
    }

}
