package de.tud.feedback.plugin;

import de.tud.feedback.ContextImporter;
import de.tud.feedback.annotation.LogDuration;
import de.tud.feedback.domain.ContextImport;
import org.openrdf.rio.*;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.MalformedURLException;

public class RdfContextImporter implements ContextImporter {

    private final RDFHandler handler;

    public RdfContextImporter(RDFHandler handler) {
        this.handler = handler;
    }

    @Override
    @LogDuration
    public void importContextFrom(ContextImport contextImport) {
        try {
            final RDFFormat format = Rio.getParserFormatForMIMEType(contextImport.getMimeType().toString());
            final RDFParser parser = Rio.createParser(format);
            final Resource resource = contextImport.getSource();

            parser.setRDFHandler(handler);
            parser.parse(resource.getInputStream(), resource.getURI().toString());

        } catch (MalformedURLException exception) {
            throw new RuntimeException("resource URL is malformed", exception);

        } catch (IOException exception) {
            throw new RuntimeException("cannot read RDF", exception);

        } catch (RDFParseException exception) {
            throw new RuntimeException("RDF is malformed", exception);

        } catch (RDFHandlerException exception) {
            throw new RuntimeException("RDF import failed", exception);
        }
    }

}
