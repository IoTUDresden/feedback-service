package de.tud.feedback.service.impl;

import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.annotation.GraphTransactional;
import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.ContextImport;
import de.tud.feedback.domain.ContextNode;
import de.tud.feedback.graph.CollectingCypherExecutor;
import de.tud.feedback.loop.Monitor;
import de.tud.feedback.repository.graph.ContextImportRepository;
import de.tud.feedback.repository.graph.ContextRepository;
import de.tud.feedback.service.ContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Provider;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.partition;
import static java.util.stream.Collectors.toList;

@Service
public class PluginContextService implements ContextService {

    private ContextImportRepository contextImportRepository;

    private FeedbackPlugin plugin;

    private Provider<CollectingCypherExecutor> executorProvider;

    private ContextRepository contextRepository;

    private Provider<Monitor> monitor;

    @PostConstruct
    public void beginUpdatesOnAllContexts() {
        newArrayList(contextRepository.findAll())
                .forEach(context -> monitor.get().monitor(context));
    }

    @Async
    @Override
    @GraphTransactional
    public void importAllOf(Context context) {
        context.getImports().forEach(contextImport -> {
            contextImport.setContext(context);
            importContextFrom(contextImport);
        });

        monitor.get().monitor(context);
    }

    @Override
    public void deleteImportsFor(Context context) {
        contextImportRepository.deleteAllWithin(context);
    }

    private void importContextFrom(ContextImport contextImport) {
        final CollectingCypherExecutor executor = executorProvider.get();

        plugin.getContextImporter(executor).importContextFrom(contextImport);
        partition(entranceNodesFrom(executor.createdNodes()), 10).forEach(nodes -> {
            contextImport.getContextNodes().addAll(nodes);
            contextImportRepository.save(contextImport);
        });
    }

    private List<ContextNode> entranceNodesFrom(Set<Long> createdNotes) {
        return createdNotes.stream()
                .map(ContextNode::fromId)
                .collect(toList());
    }

    @Autowired
    public void setContextImportRepository(ContextImportRepository repository) {
        contextImportRepository = repository;
    }

    @Autowired
    public void setPlugin(FeedbackPlugin plugin) {
        this.plugin = plugin;
    }

    @Autowired
    public void setExecutorProvider(Provider<CollectingCypherExecutor> provider) {
        this.executorProvider = provider;
    }

    @Autowired
    public void setMonitorProvider(Provider<Monitor> provider) {
        monitor = provider;
    }

    @Autowired
    public void setContextRepository(ContextRepository contextRepository) {
        this.contextRepository = contextRepository;
    }

}
