package de.tud.feedback.api.impl;

import de.tud.feedback.api.CypherExecutor;
import de.tud.feedback.api.GraphOperations;

import static de.tud.feedback.Utils.params;
import static java.lang.String.format;

public class LabelBasedGraphOperations implements GraphOperations {

    private final CypherExecutor executor;

    private final String label;

    private final String identifier;

    public LabelBasedGraphOperations(CypherExecutor executor, String label, String identifier) {
        this.executor = executor;
        this.identifier = identifier;
        this.label = label;

        executor.execute(
                format("CREATE CONSTRAINT ON (n:%s) ASSERT n.%s IS UNIQUE", label, identifier),
                params().build());

        executor.execute(
                format("CREATE INDEX ON :%s(namespace)", label),
                params().build());
    }

    @Override
    public void createConnection(String id, String type, String startId, String endId) {
        executor.execute(
                        format("MERGE (s:%s { %s: {startId} }) ", label, identifier) +
                        format("MERGE (o:%s { %s: {endId} }) ", label, identifier) +
                        format("CREATE UNIQUE (s)-[p:%s { %s: {id} }]->(o)", type, identifier) +
                               "RETURN ID(s), p, ID(o)",

                params().put("startId", startId)
                        .put("endId", endId)
                        .put("id", id)
                        .build());
    }

    @Override
    public void createNode(String id, String namespace) {
        executor.execute(
                        format("MERGE (n:%s { %s: {id}, namespace: {namespace} }) ", label, identifier) +
                               "RETURN ID(n)",

                params().put("id", id)
                        .put("namespace", namespace)
                        .build());
    }

    @Override
    public void setAdditionalLabel(String id, String additionalLabel) {
        executor.execute(
                        format("MERGE (n:%s { %s: {id} }) ", label, identifier) +
                        format("SET n :%s:%s ", label, additionalLabel) +
                               "RETURN ID(n)",

                params().put("id", id)
                        .build());
    }

    @Override
    public void setNodeProperty(String id, String name, Object value) {
        executor.execute(
                        format("MERGE (n:%s { %s: {id} }) ", label, identifier) +
                        format("SET n.%s = {value} ", name) +
                               "RETURN ID(n)",

                params().put("id", id)
                        .put("value", value)
                        .build());
    }

}
