package de.tud.feedback.plugin;

import de.tud.feedback.api.ContextImporter;
import org.openrdf.rio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.MalformedURLException;

public class RdfContextImporter implements ContextImporter {

    private static final Logger LOG = LoggerFactory.getLogger(RdfContextImporter.class);

    private final RDFHandler handler;

    public RdfContextImporter(RDFHandler handler) {
        this.handler = handler;
    }

    @Override
    public void importContextFrom(Resource resource, String mimeType) {
        try {
            final String resourceUri = resource.getURI().toString();
            final RDFFormat format = Rio.getParserFormatForMIMEType(mimeType);
            final RDFParser parser = Rio.createParser(format);

            parser.setRDFHandler(handler);
            parser.parse(resource.getInputStream(), resourceUri);

        } catch (MalformedURLException exception) {
            LOG.error("resource URL is malformed");
            throw new RuntimeException(exception);

        } catch (IOException exception) {
            LOG.error("cannot read RDF");
            throw new RuntimeException(exception);

        } catch (RDFParseException exception) {
            LOG.error("RDF is malformed");
            throw new RuntimeException(exception);

        } catch (RDFHandlerException exception) {
            LOG.error("RDF import failed");
            throw new RuntimeException(exception);
        }
    }

}
