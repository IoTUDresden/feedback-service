package de.tud.feedback.api.context;

public interface ContextImportStrategy {

    void importContextWith(CypherExecutor operations);

}
