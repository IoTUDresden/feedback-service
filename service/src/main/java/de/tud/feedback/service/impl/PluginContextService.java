package de.tud.feedback.service.impl;

import de.tud.feedback.api.context.impl.NodeCollectingCypherExecutor;
import de.tud.feedback.domain.Context;
import de.tud.feedback.repository.ContextRepository;
import de.tud.feedback.repository.PluginRepository;
import de.tud.feedback.service.ContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PluginContextService implements ContextService {

    private final ContextRepository contextRepository;

    private final PluginRepository pluginRepository;

    private final Neo4jOperations neo4jOperations;

    @Autowired
    public PluginContextService(
            ContextRepository contextRepository,
            PluginRepository pluginRepository,
            Neo4jOperations neo4jOperations) {

        this.pluginRepository = pluginRepository;
        this.contextRepository = contextRepository;
        this.neo4jOperations = neo4jOperations;
    }

    @Async
    @Override
    @Transactional
    public void importContext(Context context) {
        final NodeCollectingCypherExecutor executor = new NodeCollectingCypherExecutor(neo4jOperations);
        pluginRepository.findOne("proteus").contextImportStrategy().importContextWith(executor, context.getSource());
        context.getNodes().addAll(executor.getNodes());
        contextRepository.save(context);
    }

}
