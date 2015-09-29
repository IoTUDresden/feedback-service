package de.tud.feedback.service.impl;

import de.tud.feedback.annotation.GraphTransactional;
import de.tud.feedback.api.context.ContextImportStrategy;
import de.tud.feedback.api.context.impl.NodeCollectingCypherExecutor;
import de.tud.feedback.domain.Node;
import de.tud.feedback.domain.context.Context;
import de.tud.feedback.domain.context.ContextImport;
import de.tud.feedback.repository.ContextImportRepository;
import de.tud.feedback.repository.PluginRepository;
import de.tud.feedback.service.ContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.partition;
import static com.google.common.collect.Sets.intersection;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class PluginContextService implements ContextService {

    private static final String ORPHANED_NODE_IDS_QUERY = "START n = NODE(*) WHERE NOT n-[*..2]->() RETURN ID(n) AS ID";

    private static final Logger LOG = LoggerFactory.getLogger(PluginContextService.class);

    private final ContextImportRepository contextImports;

    private final PluginRepository plugins;

    private final Neo4jOperations neo4j;

    @Autowired
    public PluginContextService(
            ContextImportRepository contextImports,
            PluginRepository plugins,
            Neo4jOperations neo4j) {

        this.contextImports = contextImports;
        this.plugins = plugins;
        this.neo4j = neo4j;
    }

    @Async
    @Override
    @GraphTransactional
    public void importFrom(Context context) {
        context.getImports().forEach(contextImport -> {
            contextImport.setContext(context);
            importContextFrom(contextImport);
        });
    }

    private void importContextFrom(ContextImport contextImport) {
        final String plugin = contextImport.getContext().getPlugin();
        final ContextImportStrategy strategy = plugins.findOne(plugin).contextImportStrategy();
        final NodeCollectingCypherExecutor executor = new NodeCollectingCypherExecutor(neo4j);

        LOG.info("Importing {} with {} ...", contextImport.getSource(), plugin);

        strategy.importContextWith(executor, contextImport.getSource(), contextImport.getMime());
        partition(entranceNodesFrom(executor.createdNodes()), 10)
                .forEach(nodes -> {
                    contextImport.getEntranceNodes().addAll(nodes);
                    contextImports.save(contextImport); });

        LOG.info("Import of {} done", contextImport.getSource());
    }

    private List<Node> entranceNodesFrom(Set<Long> createdNotes) {
        return intersection(orphanedNodeIds(), createdNotes)
                        .stream()
                        .map(id -> {
                            Node node = new Node();
                            node.setId(id);
                            return node; })
                        .collect(toList());
    }

    private Set<Long> orphanedNodeIds() {
        return newArrayList(neo4j.query(ORPHANED_NODE_IDS_QUERY, emptyMap()).queryResults())
                .stream()
                .map(row -> ((Integer) row.get("ID")).longValue())
                .collect(toSet());
    }

}
