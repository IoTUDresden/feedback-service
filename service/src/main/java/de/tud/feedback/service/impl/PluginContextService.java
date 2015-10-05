package de.tud.feedback.service.impl;

import de.tud.feedback.annotation.GraphTransactional;
import de.tud.feedback.annotation.LogDuration;
import de.tud.feedback.annotation.LogInvocation;
import de.tud.feedback.api.ContextImporter;
import de.tud.feedback.api.FeedbackPlugin;
import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.ContextImport;
import de.tud.feedback.domain.ContextNode;
import de.tud.feedback.graph.NodeCollectingCypherExecutor;
import de.tud.feedback.graph.SimpleCypherExecutor;
import de.tud.feedback.repository.ContextImportRepository;
import de.tud.feedback.repository.ContextRepository;
import de.tud.feedback.repository.PluginRepository;
import de.tud.feedback.service.ContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Provider;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.partition;
import static com.google.common.collect.Sets.intersection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class PluginContextService implements ContextService {

    private ContextImportRepository imports;

    private ContextRepository contexts;

    private PluginRepository plugins;

    private Provider<NodeCollectingCypherExecutor> collectingExecutor;

    private Provider<SimpleCypherExecutor> simpleExecutor;

    private ResourceLoader resources;

    @PostConstruct
    public void initialize() {
        contexts.findAll().forEach(this::beginUpdatesOn);
    }

    @Override
    @LogDuration
    @GraphTransactional
    public void importAllOf(Context context) {
        context.getImports().forEach(contextImport -> {
            contextImport.setContext(context);
            importContextFrom(contextImport);
        });
    }

    @Override
    @LogInvocation
    public void beginUpdatesOn(Context context) {
        final FeedbackPlugin plugin = plugins.findOne(context.getPlugin());
        plugin.getMonitorAgentsFor(context.getId()).forEach(agent ->
                agent.start(plugin.getContextUpdater(simpleExecutor.get())));
    }

    private void importContextFrom(ContextImport contextImport) {
        final String plugin = contextImport.getContext().getPlugin();
        final NodeCollectingCypherExecutor executor = collectingExecutor.get();
        final ContextImporter importer = plugins.findOne(plugin).getContextImporter(executor);

        importer.importContextFrom(resourceFrom(contextImport), contextImport.getMime());
        partition(entranceNodesFrom(executor.createdNodes()), 10).forEach(nodes -> {
            contextImport.getEntranceNodes().addAll(nodes);
            imports.save(contextImport);
        });
    }

    private List<ContextNode> entranceNodesFrom(Set<Long> createdNotes) {
        return intersection(createdNotes, orphanedNodes())
                .stream()
                .map(id -> {
                    ContextNode node = new ContextNode();
                    node.setId(id);
                    return node;
                }).collect(toList());
    }

    private Set<Long> orphanedNodes() {
        return contexts.findOrphanedNodeIds()
                .stream()
                .map(Integer::longValue)
                .collect(toSet());
    }

    private Resource resourceFrom(ContextImport contextImport) {
        return resources.getResource(contextImport.getSource());
    }

    @Autowired
    public void setContextImportRepository(ContextImportRepository repository) {
        imports = repository;
    }

    @Autowired
    public void setContextRepository(ContextRepository repository) {
        contexts = repository;
    }

    @Autowired
    public void setPluginRepository(PluginRepository repository) {
        plugins = repository;
    }

    @Autowired
    public void setCypherExecutorProvider(Provider<NodeCollectingCypherExecutor> provider) {
        collectingExecutor = provider;
    }

    @Autowired
    public void setSimpleExecutor(Provider<SimpleCypherExecutor> provider) {
        simpleExecutor = provider;
    }

    @Autowired
    public void setResources(ResourceLoader resources) {
        this.resources = resources;
    }

}
