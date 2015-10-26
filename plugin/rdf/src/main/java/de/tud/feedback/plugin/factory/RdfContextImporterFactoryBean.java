package de.tud.feedback.plugin.factory;

import de.tud.feedback.CypherExecutor;
import de.tud.feedback.impl.LabelBasedGraphOperations;
import de.tud.feedback.plugin.RdfContextImporter;
import de.tud.feedback.plugin.rdf.GraphOperationsRdfHandler;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import static com.google.common.base.Preconditions.checkNotNull;

public class RdfContextImporterFactoryBean extends AbstractFactoryBean<RdfContextImporter> {

    private CypherExecutor executor;

    private String nodeIdentifier = "uri";

    private String nodeLabel = "RDF";

    public RdfContextImporterFactoryBean setExecutor(CypherExecutor executor) {
        this.executor = checkNotNull(executor);
        return this;
    }

    public RdfContextImporterFactoryBean setNodeIdentifier(String nodeIdentifier) {
        this.nodeIdentifier = checkNotNull(nodeIdentifier);
        return this;
    }

    public RdfContextImporterFactoryBean setNodeLabel(String nodeLabel) {
        this.nodeLabel = checkNotNull(nodeLabel);
        return this;
    }

    @Override
    protected RdfContextImporter createInstance() {
        checkNotNull(executor, "No executor defined");

        return new RdfContextImporter(
                new GraphOperationsRdfHandler(
                        new LabelBasedGraphOperations(executor, nodeLabel, nodeIdentifier)));
    }

    @Override
    public Class<?> getObjectType() {
        return RdfContextImporter.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
