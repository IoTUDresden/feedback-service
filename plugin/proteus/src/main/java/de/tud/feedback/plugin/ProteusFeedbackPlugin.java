package de.tud.feedback.plugin;

import de.tud.feedback.ContextImporter;
import de.tud.feedback.ContextUpdater;
import de.tud.feedback.CypherExecutor;
import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.loop.CommandExecutor;
import de.tud.feedback.loop.MismatchProvider;
import de.tud.feedback.loop.MonitorAgent;
import de.tud.feedback.loop.ObjectiveEvaluator;
import de.tud.feedback.plugin.factory.DogOntCompensationRepositoryFactoryBean;
import de.tud.feedback.plugin.factory.DogOntContextUpdaterFactoryBean;
import de.tud.feedback.plugin.factory.RdfContextImporterFactoryBean;
import de.tud.feedback.plugin.factory.SpelObjectiveEvaluatorFactoryBean;
import de.tud.feedback.repository.CompensationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Provider;
import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class ProteusFeedbackPlugin implements FeedbackPlugin {

    @Autowired Provider<RdfContextImporter> importerProvider;

    @Autowired RdfContextImporterFactoryBean importerFactory;

    @Autowired Provider<DogOntContextUpdater> updaterProvider;

    @Autowired DogOntContextUpdaterFactoryBean updaterFactory;

    @Autowired Provider<OpenHabMonitorAgent> monitorAgentProvider;

    @Autowired SpelObjectiveEvaluatorFactoryBean evaluatorFactory;

    @Autowired Provider<SpelObjectiveEvaluator> evaluatorProvider;

    @Autowired DogOntCompensationRepositoryFactoryBean compensationRepositoryFactory;

    @Autowired Provider<DogOntCompensationRepository> compensationRepositoryProvider;

    @Autowired Provider<OpenHabCommandExecutor> commandExecutorProvider;

    @Autowired Provider<PeerMetricsMonitorAgent> peerMetricsMonitorAgentProvider;

    @Autowired Provider<PeerMonitorAgent> peerMonitorAgentProvider;

    @Override
    public ContextImporter getContextImporter(CypherExecutor executor) {
        importerFactory.setExecutor(executor);
        return importerProvider.get();
    }

    @Override
    public ContextUpdater getContextUpdater(CypherExecutor executor) {
        updaterFactory.setExecutor(executor);
        return updaterProvider.get();
    }

    @Override
    public ObjectiveEvaluator getObjectiveEvaluator(CypherExecutor executor) {
        evaluatorFactory.setExecutor(executor);
        return evaluatorProvider.get();
    }

    @Override
    public MismatchProvider getMismatchProvider() {
        return new SpelMismatchProvider();
    }

    @Override
    public Collection<MonitorAgent> getMonitorAgents() {
        return newArrayList(monitorAgentProvider.get(),peerMonitorAgentProvider.get(),peerMetricsMonitorAgentProvider.get());
    }

    @Override
    public CommandExecutor getExecutor() {
        return commandExecutorProvider.get();
    }

    @Override
    public CompensationRepository getCompensationRepository(CypherExecutor executor) {
        compensationRepositoryFactory.setExecutor(executor);
        return compensationRepositoryProvider.get();
    }


}
