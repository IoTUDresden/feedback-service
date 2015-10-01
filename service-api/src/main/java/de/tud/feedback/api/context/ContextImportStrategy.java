package de.tud.feedback.api.context;

import de.tud.feedback.api.graph.CypherExecutor;
import org.springframework.core.io.Resource;

public interface ContextImportStrategy {

    void importContextWith(CypherExecutor executor, Resource resource, String mimeType);

}
