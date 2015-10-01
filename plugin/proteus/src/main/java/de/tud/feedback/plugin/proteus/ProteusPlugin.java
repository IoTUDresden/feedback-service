package de.tud.feedback.plugin.proteus;

import de.tud.feedback.api.ComponentProvider;
import de.tud.feedback.api.FeedbackServicePlugin;
import de.tud.feedback.api.context.ContextImportStrategy;
import de.tud.feedback.plugin.rdf.RdfContextImportStrategy;

import javax.inject.Inject;

@FeedbackServicePlugin(name = "proteus", componentsProvidedBy = ProteusPlugin.class)
class ProteusPlugin implements ComponentProvider {

    @Inject
    private RdfContextImportStrategy contextImportStrategy;

    @Override
    public ContextImportStrategy contextImportStrategy() {
        return contextImportStrategy;
    }

}
