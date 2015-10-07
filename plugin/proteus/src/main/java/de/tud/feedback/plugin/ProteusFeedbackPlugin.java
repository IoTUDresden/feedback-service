package de.tud.feedback.plugin;

import de.tud.feedback.api.*;
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

    @Autowired RdfContextImporterFactoryBean importerFactory;

    @Autowired OpenHabMonitorAgent monitorAgent;

    @Override
    public ContextImporter contextImporter(CypherExecutor executor) {
        importerFactory.setExecutor(executor);
        return importerProvider.get();
    }

    @Override
    public ContextUpdater contextUpdater(CypherExecutor executor) {
        return new DogOntContextUpdater(executor);
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
