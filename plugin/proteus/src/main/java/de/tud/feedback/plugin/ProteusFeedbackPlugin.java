package de.tud.feedback.plugin;

import de.tud.feedback.api.CypherExecutor;
import de.tud.feedback.api.FeedbackPlugin;
import de.tud.feedback.api.context.ContextImporter;
import de.tud.feedback.plugin.context.RdfContextImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Provider;

@Component
public class ProteusFeedbackPlugin implements FeedbackPlugin {

    public static final String NAME = "Proteus";

    @Autowired
    private RdfContextImporterFactoryBean contextImporterFactoryBean;

    @Autowired
    private Provider<RdfContextImporter> contextImporterProvider;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public ContextImporter getContextImporter(CypherExecutor executor) {
        contextImporterFactoryBean.setExecutor(executor);
        return contextImporterProvider.get();
    }

    @Override
    public String toString() {
        return NAME;
    }

}
