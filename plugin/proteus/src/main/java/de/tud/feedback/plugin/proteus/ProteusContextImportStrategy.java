package de.tud.feedback.plugin.proteus;

import de.tud.feedback.api.context.ContextImportException;
import de.tud.feedback.api.context.ContextImportStrategy;
import de.tud.feedback.api.context.CypherExecutor;
import org.openrdf.rio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Component
class ProteusContextImportStrategy implements ContextImportStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(ProteusContextImportStrategy.class);

    private final RDFParser parser = Rio.createParser(RDFFormat.RDFXML);

    private RdfHandlerFactory handler;

    private String dogontoUrl;

    @Autowired
    public void setDogontoUrl(@Value("${dogonto.url}") String dogontoUrl) {
        this.dogontoUrl = dogontoUrl;
    }

    @Autowired
    public void setHandlerFactory(RdfHandlerFactory handler) {
        this.handler = handler;
    }

    @Override
    public void importContextWith(CypherExecutor executor) {
        try {
            final URL url = new URL(dogontoUrl);

            parser.setRDFHandler(handler.basedOn(executor));
            parser.parse(url.openStream(), dogontoUrl);

        } catch (MalformedURLException exception) {
            LOG.error("dogonto URL is malformed");
            throw new ContextImportException(exception);

        } catch (IOException exception) {
            LOG.error("cannot read ontology from " + dogontoUrl);
            throw new ContextImportException(exception);

        } catch (RDFParseException exception) {
            LOG.error("ontology is malformed");
            throw new ContextImportException(exception);

        } catch (RDFHandlerException exception) {
            LOG.error("ontology import failed due to the handler");
            throw new ContextImportException(exception);
        }
    }

}
