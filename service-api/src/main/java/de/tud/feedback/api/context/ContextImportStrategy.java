package de.tud.feedback.api.context;

import org.springframework.data.neo4j.template.Neo4jOperations;

public interface ContextImportStrategy {

    void importContextWith(Neo4jOperations operations);

}
