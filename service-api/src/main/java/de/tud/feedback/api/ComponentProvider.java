package de.tud.feedback.api;

import de.tud.feedback.api.context.ContextImportStrategy;
import de.tud.feedback.api.context.ContextUpdateStrategy;

public interface ComponentProvider {

    ContextImportStrategy contextImportStrategy();

    ContextUpdateStrategy contextUpdateStrategy();

}
