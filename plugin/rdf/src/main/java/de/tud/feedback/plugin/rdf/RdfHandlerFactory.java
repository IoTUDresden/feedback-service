package de.tud.feedback.plugin.rdf;

import de.tud.feedback.api.context.CypherExecutor;
import org.openrdf.rio.RDFHandler;

interface RdfHandlerFactory {

    RDFHandler basedOn(CypherExecutor executor);

}
