package de.tud.feedback.api.context;

import org.springframework.core.io.Resource;

public interface ContextImporter {

    void importContextFrom(Resource resource, String mimeType);

}
