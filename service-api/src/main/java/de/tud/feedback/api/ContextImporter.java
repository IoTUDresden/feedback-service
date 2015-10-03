package de.tud.feedback.api;

import org.springframework.core.io.Resource;

public interface ContextImporter {

    void importContextFrom(Resource resource, String mimeType);

}
