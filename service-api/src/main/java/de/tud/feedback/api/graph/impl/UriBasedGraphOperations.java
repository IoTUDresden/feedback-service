package de.tud.feedback.api.graph.impl;

import com.google.common.collect.ImmutableMap;
import de.tud.feedback.api.graph.CypherExecutor;
import de.tud.feedback.api.graph.GraphOperations;

import static java.lang.String.format;

public class UriBasedGraphOperations implements GraphOperations {

    private final CypherExecutor executor;

    public UriBasedGraphOperations(CypherExecutor executor) {
        this.executor = executor;

        executor.execute("CREATE CONSTRAINT ON (n:UriBased) ASSERT n.uri IS UNIQUE", params().build());
    }

    @Override
    public void mergeConnection(String subject, String predicate, String object, String name) {
        executor.execute(format(
                        "MERGE (s:UriBased { uri: {subjectUri} }) " +
                        "MERGE (o:UriBased { uri: {objectUri} }) " +
                        "CREATE UNIQUE (s)-[p:%s { uri: {predicateUri} }]->(o)" +
                        "RETURN ID(s), p, ID(o)", name),

                params().put("subjectUri", subject)
                        .put("predicateUri", predicate)
                        .put("objectUri", object)
                        .build());
    }

    @Override
    public void mergeNode(String uri, String name) {
        executor.execute(
                        "MERGE (n:UriBased { uri: {uri} }) " +
                        "SET n.name = {name} " +
                        "RETURN ID(n)",

                params().put("uri", uri)
                        .put("name", name)
                        .build());
    }

    @Override
    public void setLabel(String uri, String label) {
        executor.execute(format(
                        "MERGE (n:UriBased { uri: {uri} }) " +
                        "SET n :UriBased:%s " +
                        "RETURN (n)", label),

                params().put("uri", uri)
                        .build());
    }

    @Override
    public void setProperty(String uri, String name, Object value) {
        executor.execute(format(
                        "MERGE (n:UriBased { uri: {uri} }) " +
                        "SET n.%s = {value} " +
                        "RETURN ID(n)", name),

                params().put("uri", uri)
                        .put("value", value)
                        .build());
    }

    private ImmutableMap.Builder<String, Object> params() {
        return new ImmutableMap.Builder<>();
    }

}
