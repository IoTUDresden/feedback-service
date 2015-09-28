package de.tud.feedback.plugin.proteus.graph;

import com.google.common.collect.ImmutableMap;
import de.tud.feedback.api.context.CypherExecutor;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import static java.lang.String.format;

public class CypherGraphOperations implements GraphOperations {

    private final CypherExecutor executor;

    public CypherGraphOperations(CypherExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void connectNodes(URI subject, URI predicate, Value object, String name) {
        executor.execute(format(
                        "MATCH (s {uri: {subjectUri}}), (o {uri: {objectUri}}) \n" +
                        "CREATE (s)-[p:%s {uri: {predicateUri}}]->(o) \n" +
                        "RETURN s, p, o", name),

                params().put("subjectUri", subject.stringValue())
                        .put("predicateUri", predicate.stringValue())
                        .put("objectUri", object.stringValue())
                        .build());
    }

    @Override
    public void createNode(URI uri, String name) {
        executor.execute(
                        "CREATE (n {name: {name}, uri: {uri}}) \n" +
                        "RETURN n",

                params().put("uri", uri.stringValue())
                        .put("name", name)
                        .build());
    }

    @Override
    public void setNodeLabel(URI uri, String label) {
        executor.execute(format(
                        "MATCH (n {uri: {uri}}) \n" +
                        "SET n :%s \n" +
                        "RETURN n", label),

                params().put("uri", uri.stringValue())
                        .build());
    }

    @Override
    public void setNodeProperty(URI uri, String name, Literal value) {
        executor.execute(format(
                        "MATCH (n {uri: {uri}}) \n" +
                        "SET n.%s = {value} \n" +
                        "RETURN n", name),

                params().put("uri", uri.stringValue())
                        .put("value", converted(value))
                        .put("name", name)
                        .build());
    }

    private ImmutableMap.Builder<String, Object> params() {
        return new ImmutableMap.Builder<String, Object>();
    }

    private Object converted(Literal object) {
        URI type = object.getDatatype();

        if (object.getDatatype() == null) {
            return object.stringValue();
        }

        String localName = type.getLocalName();

        if (localName.toLowerCase().contains("integer") || localName.equals("long")) {
            return object.longValue();
        } else if (localName.toLowerCase().contains("short")) {
            return object.shortValue();
        } else if (localName.equals("byte")) {
            return object.byteValue();
        } else if (localName.equals("char")) {
            return object.byteValue();
        } else if (localName.equals("float")) {
            return object.floatValue();
        } else if (localName.equals("double")) {
            return object.doubleValue();
        } else if (localName.equals("boolean")) {
            return object.booleanValue();
        } else {
            return object.stringValue();
        }
    }

}
