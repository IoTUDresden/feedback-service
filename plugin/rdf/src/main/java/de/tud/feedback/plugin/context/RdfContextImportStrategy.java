package de.tud.feedback.plugin.context;

import de.tud.feedback.api.context.ContextImportException;
import de.tud.feedback.api.context.ContextImportStrategy;
import de.tud.feedback.api.graph.CypherExecutor;
import de.tud.feedback.plugin.rdf.RdfHandlerFactory;
import org.openrdf.rio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;

@Component
public class RdfContextImportStrategy implements ContextImportStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(RdfContextImportStrategy.class);

    private RdfHandlerFactory handler;

    @Autowired
    public void setHandlerFactory(RdfHandlerFactory handler) {
        this.handler = handler;
    }

    @Override
    public void importContextWith(CypherExecutor executor, Resource resource, String mimeType) {
        try {
            final String resourceUri = resource.getURI().toString();
            final RDFFormat format = Rio.getParserFormatForMIMEType(mimeType);
            final RDFParser parser = Rio.createParser(format);

            parser.setRDFHandler(handler.basedOn(executor));
            parser.parse(resource.getInputStream(), resourceUri);

        } catch (MalformedURLException exception) {
            LOG.error("resource URL is malformed");
            throw new ContextImportException(exception);

        } catch (IOException exception) {
            LOG.error("cannot read RDF");
            throw new ContextImportException(exception);

        } catch (RDFParseException exception) {
            LOG.error("RDF is malformed");
            throw new ContextImportException(exception);

        } catch (RDFHandlerException exception) {
            LOG.error("RDF import failed");
            throw new ContextImportException(exception);
        }
    }

}
