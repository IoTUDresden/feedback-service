package de.tud.feedback.plugin.factory;

import de.tud.feedback.api.CypherExecutor;
import de.tud.feedback.api.impl.LabelBasedGraphOperations;
import de.tud.feedback.plugin.RdfContextImporter;
import de.tud.feedback.plugin.rdf.GraphOperationsRdfHandler;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class RdfContextImporterFactoryBean extends AbstractFactoryBean<RdfContextImporter> {

    private CypherExecutor executor;

    private String nodeIdentifier;

    private String nodeLabel;

    public RdfContextImporterFactoryBean setExecutor(CypherExecutor executor) {
        this.executor = executor;
        return this;
    }

    public RdfContextImporterFactoryBean setNodeIdentifier(String nodeIdentifier) {
        this.nodeIdentifier = nodeIdentifier;
        return this;
    }

    public RdfContextImporterFactoryBean setNodeLabel(String nodeLabel) {
        this.nodeLabel = nodeLabel;
        return this;
    }

    @Override
    protected RdfContextImporter createInstance() {
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

    public static RdfContextImporterFactoryBean build() {
        return new RdfContextImporterFactoryBean();
    }

}
