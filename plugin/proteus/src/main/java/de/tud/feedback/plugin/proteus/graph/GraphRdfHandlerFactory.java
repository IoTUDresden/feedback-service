package de.tud.feedback.plugin.proteus.graph;

import de.tud.feedback.api.context.CypherExecutor;
import de.tud.feedback.plugin.proteus.RdfHandlerFactory;
import org.openrdf.rio.RDFHandler;
import org.springframework.stereotype.Component;

@Component
class GraphRdfHandlerFactory implements RdfHandlerFactory {

    @Override
    public RDFHandler basedOn(CypherExecutor executor) {
        return new GraphOperationsRdfHandler(
                new CypherGraphOperations(executor));
    }

}
