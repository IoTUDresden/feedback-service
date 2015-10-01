package de.tud.feedback.api.context;

import org.springframework.core.io.Resource;

public interface ContextImportStrategy {

    void importContextWith(CypherExecutor operations, Resource resource, String mimeType);

}
