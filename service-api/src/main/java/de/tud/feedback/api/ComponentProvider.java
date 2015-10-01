package de.tud.feedback.api;

import de.tud.feedback.api.context.ContextImportStrategy;
import de.tud.feedback.api.context.ContextUpdateStrategy;

import javax.inject.Provider;

public interface ComponentProvider {

    Provider<? extends ContextImportStrategy> contextImportStrategy();

    Provider<? extends ContextUpdateStrategy> contextUpdateStrategy();

}
