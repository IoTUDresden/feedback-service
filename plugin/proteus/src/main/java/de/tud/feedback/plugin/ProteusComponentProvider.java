package de.tud.feedback.plugin;

import de.tud.feedback.api.ComponentProvider;
import de.tud.feedback.api.annotations.FeedbackServicePlugin;
import de.tud.feedback.api.context.ContextImportStrategy;
import de.tud.feedback.api.context.ContextUpdateStrategy;
import de.tud.feedback.plugin.context.OpenHabContextUpdateStrategy;
import de.tud.feedback.plugin.context.RdfContextImportStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Provider;

@FeedbackServicePlugin(name = "proteus", componentsProvidedBy = ProteusComponentProvider.class)
public class ProteusComponentProvider implements ComponentProvider {

    @Autowired
    Provider<RdfContextImportStrategy> contextImportStrategy;

    @Autowired
    Provider<OpenHabContextUpdateStrategy> contextUpdateStrategy;

    @Override
    public Provider<? extends ContextImportStrategy> contextImportStrategy() {
        return contextImportStrategy;
    }

    @Override
    public Provider<? extends ContextUpdateStrategy> contextUpdateStrategy() {
        return contextUpdateStrategy;
    }

}
