package de.tud.feedback.plugin.proteus;

import de.tud.feedback.api.context.CypherExecutor;
import org.openrdf.rio.RDFHandler;

public interface RdfHandlerFactory {

    RDFHandler basedOn(CypherExecutor executor);

}
