package de.tud.feedback.service.impl;

import de.tud.feedback.annotation.GraphTransactional;
import de.tud.feedback.api.CypherExecutor;
import de.tud.feedback.api.FeedbackPlugin;
import de.tud.feedback.api.annotation.LogDuration;
import de.tud.feedback.api.annotation.LogInvocation;
import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.ContextImport;
import de.tud.feedback.domain.ContextNode;
import de.tud.feedback.loop.Monitor;
import de.tud.feedback.repository.ContextImportRepository;
import de.tud.feedback.repository.ContextRepository;
import de.tud.feedback.service.ContextService;
import de.tud.feedback.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.partition;
import static com.google.common.collect.Sets.intersection;
import static java.util.stream.Collectors.toList;

@Service
public class PluginContextService implements ContextService {

    private ContextImportRepository imports;

    private FeedbackPlugin plugin;

    private CypherExecutor executor;

    private ResourceLoader resources;

    private KnowledgeService knowledge;

    private ContextRepository contexts;

    private Monitor monitor;

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
    @PostConstruct
    public void beginContextUpdates() {
        long numberOfContexts = contexts.count();

        if (numberOfContexts > 1) {
            throw new RuntimeException("Multiple contexts are not supported yet");

        } else if (numberOfContexts > 0) {
            monitor.start();
        }
    }

    private void importContextFrom(ContextImport contextImport) {
        plugin.getContextImporter(executor)
                .importContextFrom(resourceFrom(contextImport), contextImport.getMime());

        partition(entranceNodesFrom(executor.createdNodes()), 10).forEach(nodes -> {
            contextImport.getEntranceNodes().addAll(nodes);
            imports.save(contextImport);
        });
    }

    private List<ContextNode> entranceNodesFrom(Set<Long> createdNotes) {
        return intersection(createdNotes, knowledge.findOrphanedNodes()).stream()
                .map(ContextNode::fromId)
                .collect(toList());
    }

    private Resource resourceFrom(ContextImport contextImport) {
        return resources.getResource(contextImport.getSource());
    }

    @Autowired
    public void setContextImports(ContextImportRepository repository) {
        imports = repository;
    }

    @Autowired
    public void setPlugin(FeedbackPlugin plugin) {
        this.plugin = plugin;
    }

    @Autowired
    public void setExecutor(CypherExecutor executor) {
        this.executor = executor;
    }

    @Autowired
    public void setResources(ResourceLoader resources) {
        this.resources = resources;
    }

    @Autowired
    public void setKnowledge(KnowledgeService knowledge) {
        this.knowledge = knowledge;
    }

    @Autowired
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    @Autowired
    public void setContexts(ContextRepository contexts) {
        this.contexts = contexts;
    }

}
