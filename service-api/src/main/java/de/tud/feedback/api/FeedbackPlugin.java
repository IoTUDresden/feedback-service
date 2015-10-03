package de.tud.feedback.api;

import de.tud.feedback.api.context.ContextImporter;

public interface FeedbackPlugin {

    String name();

    ContextImporter getContextImporter(CypherExecutor executor);

}
