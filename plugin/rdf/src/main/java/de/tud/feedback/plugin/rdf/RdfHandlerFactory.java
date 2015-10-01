package de.tud.feedback.plugin.rdf;

import de.tud.feedback.api.graph.CypherExecutor;
import org.openrdf.rio.RDFHandler;

public interface RdfHandlerFactory {

    RDFHandler basedOn(CypherExecutor executor);

}
