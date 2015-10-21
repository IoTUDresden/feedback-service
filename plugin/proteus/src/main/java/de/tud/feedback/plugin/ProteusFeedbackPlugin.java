package de.tud.feedback.plugin;

import de.tud.feedback.ContextImporter;
import de.tud.feedback.ContextUpdater;
import de.tud.feedback.CypherExecutor;
import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.loop.MismatchProvider;
import de.tud.feedback.loop.MonitorAgent;
import de.tud.feedback.loop.ObjectiveEvaluator;
import de.tud.feedback.plugin.factory.DogOntContextUpdaterFactoryBean;
import de.tud.feedback.plugin.factory.OpenHabMonitorAgentFactoryBean;
import de.tud.feedback.plugin.factory.RdfContextImporterFactoryBean;
import de.tud.feedback.plugin.factory.SpelObjectiveEvaluatorFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.inject.Provider;
import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class ProteusFeedbackPlugin implements FeedbackPlugin {

    public static final String NAME = "proteus";

    @Autowired Provider<RdfContextImporter> importerProvider;

    @Autowired RdfContextImporterFactoryBean importerFactory;

    @Autowired Provider<DogOntContextUpdater> updaterProvider;

    @Autowired DogOntContextUpdaterFactoryBean updaterFactory;

    @Autowired Provider<OpenHabMonitorAgent> monitorAgentProvider;

    @Autowired SpelObjectiveEvaluatorFactoryBean evaluatorFactory;

    @Autowired Provider<SpelObjectiveEvaluator> evaluatorProvider;

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
        return newArrayList(monitorAgentProvider.get());
    }

    @Autowired
    void configureRdfContextImporters(
            RdfContextImporterFactoryBean factoryBean
    ) {
        factoryBean
                .setNodeLabel(StringUtils.capitalize(ProteusFeedbackPlugin.NAME));
    }

    @Autowired
    void configureOpenHabMonitorAgents(
            OpenHabMonitorAgentFactoryBean factoryBean,
            @Value("${openHab.host:localhost}") String host,
            @Value("${openHab.port:8080}") int port,
            @Value("${openHab.delta:0.01}") Double delta,
            @Value("${openHab.pollingSeconds:1}") Integer pollingSeconds
    ) {
        factoryBean
                .setNumberStateChangeDelta(delta)
                .setPollingSeconds(pollingSeconds)
                .setHost(host)
                .setPort(port);
    }

    @Autowired
    void configureDogOntContextUpdaters(
            DogOntContextUpdaterFactoryBean factoryBean,
            @Value("${dogOnt.stateNodePrefix:State_}") String stateNodePrefix
    ) {
        factoryBean.setStateNodePrefix(stateNodePrefix);
    }

}
