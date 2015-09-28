package de.tud.feedback.plugin.proteus;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import de.tud.feedback.api.context.CypherOperations;
import org.openrdf.model.*;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.String.format;

class CypherOperationsRdfHandler extends RDFHandlerBase {

    private static final String TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    private final Map<String, List<String>> subjects = newHashMap();

    private final List<String> objects = newArrayList();

    private final List<String> connections = newArrayList();

    private final CypherOperations operations;

    public CypherOperationsRdfHandler(CypherOperations operations) {
        this.operations = operations;
    }

    @Override
    public void handleStatement(Statement statement) throws RDFHandlerException {
        final Resource subjectResource = statement.getSubject();
        final Value objectValue = statement.getObject();
        final URI predicate = statement.getPredicate();

        if (subjectResource instanceof BNode || objectValue instanceof BNode) {
            return;
        }

        final URI subject = (URI) subjectResource;

        prepareSubject(subject);
        handleTriplet(subject, predicate, objectValue);
    }

    private void prepareSubject(URI subject) {
        if (!subjectExists(subject)) {
            create(subject);
            flagSubjectExisting(subject);
        }
    }

    private void handleTriplet(URI subject, URI predicate, Value object) {
        if (isType(predicate)) {
            handleType(subject, (URI) object);

        } else if (object instanceof Literal) {
            setProperty(subject, nameFor(predicate), (Literal) object);

        } else {
            handleResource(subject, predicate, (URI) object);
        }
    }

    private void handleResource(URI subject, URI predicate, URI object) {
        if (!objectExists(object) && !subjectExists(object)) {
            create(object);
            flagObjectExisting(object);
        }

        if (!connectionExists(subject, predicate, object)) {
            connect(subject, predicate, object);
            flagConnectionExisting(subject, predicate, object);
        }
    }

    private void handleType(URI subject, URI object) {
        String label = nameFor(object);

        if (!subjectLabelExists(subject, label)) {
            setLabel(subject, label);
            flagSubjectLabelExisting(subject, label);
        }
    }

    private void connect(URI subject, URI predicate, Value object) {
        operations.execute(format(
                        "MATCH (s {uri: {subjectUri}}), (o {uri: {objectUri}}) \n" +
                        "CREATE (s)-[p:%s {uri: {predicateUri}}]->(o) \n" +
                        "RETURN s, p, o", nameFor(predicate)),

                params().put("subjectUri", subject.stringValue())
                        .put("predicateUri", predicate.stringValue())
                        .put("objectUri", object.stringValue())
                        .build());
    }

    private void create(URI uri) {
        operations.execute(
                        "CREATE (n {name: {name}, uri: {uri}}) \n" +
                        "RETURN n",

                params().put("uri", uri.stringValue())
                        .put("name", nameFor(uri))
                        .build());
    }

    private void setLabel(URI uri, String label) {
        operations.execute(format(
                        "MATCH (n {uri: {uri}}) \n" +
                        "SET n :%s \n" +
                        "RETURN n", label),

                params().put("uri", uri.stringValue())
                        .build());
    }

    private void setProperty(URI uri, String name, Literal value) {
        operations.execute(format(
                        "MATCH (n {uri: {uri}}) \n" +
                        "SET n.%s = {value} \n" +
                        "RETURN n", name),

                params().put("uri", uri.stringValue())
                        .put("value", converted(value))
                        .put("name", name)
                        .build());
    }

    private String cacheKeyFor(URI subject, URI predicate, URI object) {
        return format("(%s)-[%s]->(%s)",
                nameFor(subject), nameFor(predicate), nameFor(object));
    }

    private ImmutableMap.Builder<String, Object> params() {
        return new ImmutableMap.Builder<String, Object>();
    }

    private boolean isType(URI predicate) {
        return predicate.stringValue().equals(TYPE_URI);
    }

    private String nameFor(URI uri) {
        if (Strings.isNullOrEmpty(uri.getLocalName())) {
            return uri.stringValue()
                    .replace("http://", "")
                    .replaceAll("[^\\pL\\pN\\p{Pc}]", "_");
        } else {
            return uri.getLocalName();
        }
    }

    private boolean subjectLabelExists(URI subject, String label) {
        return subjects.get(subject.stringValue()).contains(label);
    }

    private boolean subjectExists(URI subject) {
        return subjects.containsKey(subject.stringValue());
    }

    private boolean connectionExists(URI subject, URI predicate, Value object) {
        return connections.contains(cacheKeyFor(subject, predicate, (URI) object));
    }

    private boolean objectExists(Value object) {
        return objects.contains(object.stringValue());
    }

    private void flagSubjectExisting(URI subject) {
        subjects.put(subject.stringValue(), new ArrayList<String>());
    }

    private void flagConnectionExisting(URI subject, URI predicate, Value object) {
        connections.add(cacheKeyFor(subject, predicate, (URI) object));
    }

    private boolean flagObjectExisting(Value object) {
        return objects.add(object.stringValue());
    }

    private boolean flagSubjectLabelExisting(URI subject, String label) {
        return subjects.get(subject.stringValue()).add(label);
    }

    private Object converted(Literal object) {
        URI type = object.getDatatype();

        if (object.getDatatype() == null) {
            return object.stringValue();
        }

        String localName = nameFor(type);

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
