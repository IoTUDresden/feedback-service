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

        executor.execute("CREATE CONSTRAINT ON (n:UriBased) ASSERT n.uri IS UNIQUE", params().build());
    }

    @Override
    public void mergeConnection(URI subject, URI predicate, Value object, String name) {
        executor.execute(format(
                        "MERGE (s:UriBased { uri: {subjectUri} }) " +
                        "MERGE (o:UriBased { uri: {objectUri} }) " +
                        "CREATE UNIQUE (s)-[p:%s { uri: {predicateUri} }]->(o)" +
                        "RETURN ID(s), p, ID(o)", name),

                params().put("subjectUri", subject.stringValue())
                        .put("predicateUri", predicate.stringValue())
                        .put("objectUri", object.stringValue())
                        .build());
    }

    @Override
    public void mergeNode(URI uri, String name) {
        executor.execute(
                        "MERGE (n:UriBased { uri: {uri} }) " +
                        "SET n.name = {name} " +
                        "RETURN ID(n)",

                params().put("uri", uri.stringValue())
                        .put("name", name)
                        .build());
    }

    @Override
    public void setLabel(URI uri, String label) {
        executor.execute(format(
                        "MERGE (n:UriBased { uri: {uri} }) " +
                        "SET n :UriBased:%s " +
                        "RETURN (n)", label),

                params().put("uri", uri.stringValue())
                        .build());
    }

    @Override
    public void setProperty(URI uri, String name, Literal value) {
        executor.execute(format(
                        "MERGE (n:UriBased { uri: {uri} }) " +
                        "SET n.%s = {value} " +
                        "RETURN ID(n)", name),

                params().put("uri", uri.stringValue())
                        .put("value", converted(value))
                        .put("name", name)
                        .build());
    }

    private ImmutableMap.Builder<String, Object> params() {
        return new ImmutableMap.Builder<>();
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
