package de.tud.feedback.plugin;

import de.tud.feedback.api.*;
import de.tud.feedback.plugin.factory.DogOntContextUpdaterFactoryBean;
import de.tud.feedback.plugin.factory.RdfContextImporterFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Provider;
import java.util.Collection;

import static java.util.Collections.singletonList;

@Component
public class ProteusFeedbackPlugin implements FeedbackPlugin {

    public static final String NAME = "proteus";

    @Autowired Provider<RdfContextImporter> importerProvider;

    @Autowired Provider<DogOntContextUpdater> updaterProvider;

    @Autowired RdfContextImporterFactoryBean importerFactory;

    @Autowired DogOntContextUpdaterFactoryBean updaterFactory;

    @Autowired OpenHabMonitorAgent monitorAgent;

    @Override
    public ContextImporter contextImporter(CypherExecutor executor) {
        importerFactory.setExecutor(executor);
        return importerProvider.get();
    }

    @Override
    public ContextUpdater contextUpdaterFor(ContextReference context, CypherExecutor executor) {
        updaterFactory.setExecutor(executor);
        return updaterProvider.get().on(context);
    }

    @Override
    public Collection<MonitorAgent> monitorAgents() {
        return singletonList(monitorAgent);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String toString() {
        return name();
    }

}
