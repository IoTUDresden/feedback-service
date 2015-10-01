package de.tud.feedback.plugin.rdf;

import de.tud.feedback.api.context.CypherExecutor;
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
