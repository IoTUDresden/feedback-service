package de.tud.feedback.plugin;

import de.tud.feedback.api.ComponentProvider;
import de.tud.feedback.api.annotations.FeedbackServicePlugin;
import de.tud.feedback.api.context.ContextImportStrategy;
import de.tud.feedback.api.context.ContextUpdateStrategy;
import de.tud.feedback.plugin.context.ProteusContextUpdateStrategy;
import de.tud.feedback.plugin.context.RdfContextImportStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@FeedbackServicePlugin(name = "proteus", componentsProvidedBy = ProteusComponentProvider.class)
public class ProteusComponentProvider implements ComponentProvider {

    @Autowired
    RdfContextImportStrategy contextImportStrategy;

    @Autowired
    ProteusContextUpdateStrategy contextUpdateStrategy;

    @Override
    public ContextImportStrategy contextImportStrategy() {
        return contextImportStrategy;
    }

    @Override
    public ContextUpdateStrategy contextUpdateStrategy() {
        return contextUpdateStrategy;
    }

}
