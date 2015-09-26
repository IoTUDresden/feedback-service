package de.tud.feedback.plugin.proteus;

import de.tud.feedback.api.context.ContextImportException;
import de.tud.feedback.api.context.ContextImportStrategy;
import org.neo4j.graphdb.GraphDatabaseService;
import org.openrdf.rio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Component
class ProteusContextImportStrategy implements ContextImportStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(ProteusContextImportStrategy.class);

    private final RDFParser parser = Rio.createParser(RDFFormat.RDFXML);

    private String dogontoUrl;

    @Autowired
    public void setDogontoUrl(@Value("${dogonto.url}") String dogontoUrl) {
        this.dogontoUrl = dogontoUrl;
    }

    @Override
    public void importContextWith(Neo4jOperations operations) {
        try {
            final URL url = new URL(dogontoUrl);

            parser.setRDFHandler(new Neo4jRdfHandler(operations));
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
