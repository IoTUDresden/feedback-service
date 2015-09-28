package de.tud.feedback.api.context;

public interface ContextImportStrategy {

    void importContextWith(CypherOperations operations);

}
