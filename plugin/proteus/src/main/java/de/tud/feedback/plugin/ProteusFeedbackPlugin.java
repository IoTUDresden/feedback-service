package de.tud.feedback.plugin;

import com.google.common.collect.ImmutableList;
import de.tud.feedback.api.*;
import de.tud.feedback.plugin.factory.DogOntContextUpdaterFactoryBean;
import de.tud.feedback.plugin.factory.OpenHabMonitorAgentFactoryBean;
import de.tud.feedback.plugin.factory.RdfContextImporterFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Provider;
import java.util.Collection;

@Component
public class ProteusFeedbackPlugin implements FeedbackPlugin {

    public static final String NAME = "Proteus";

    @Autowired RdfContextImporterFactoryBean contextImporterFactoryBean;

    @Autowired
    OpenHabMonitorAgentFactoryBean openHabMonitorAgentFactoryBean;

    @Autowired
    DogOntContextUpdaterFactoryBean dogOntContextUpdaterFactoryBean;

    @Autowired Provider<RdfContextImporter> contextImporterProvider;

    @Autowired Provider<OpenHabMonitorAgent> openHabMonitorAgentProvider;

    @Autowired Provider<DogOntContextUpdater> dogOntContextUpdaterProvider;

    @Override
    public ContextImporter getContextImporter(CypherExecutor executor) {
        contextImporterFactoryBean.setExecutor(executor);
        return contextImporterProvider.get();
    }

    @Override
    public ContextUpdater getContextUpdater(CypherExecutor executor) {
        dogOntContextUpdaterFactoryBean.setExecutor(executor);
        return dogOntContextUpdaterProvider.get();
    }

    @Override
    public Collection<MonitorAgent> getMonitorAgents() {
        return ImmutableList.<MonitorAgent>builder()
                .add(openHabMonitorAgentProvider.get())
                .build();
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
