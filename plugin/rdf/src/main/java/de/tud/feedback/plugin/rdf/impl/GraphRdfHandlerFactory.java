package de.tud.feedback.plugin.rdf.impl;

import de.tud.feedback.api.graph.CypherExecutor;
import de.tud.feedback.api.graph.impl.UriBasedGraphOperations;
import de.tud.feedback.plugin.rdf.RdfHandlerFactory;
import org.openrdf.rio.RDFHandler;
import org.springframework.stereotype.Component;

@Component
class GraphRdfHandlerFactory implements RdfHandlerFactory {

    @Override
    public RDFHandler basedOn(CypherExecutor executor) {
        return new GraphOperationsRdfHandler(
                new UriBasedGraphOperations(executor));
    }

}
