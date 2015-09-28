package de.tud.feedback.plugin.proteus.graph;

import com.google.common.base.Strings;
import org.openrdf.model.*;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.String.format;

class GraphOperationsRdfHandler extends RDFHandlerBase {

    private static final String TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    private final Map<String, List<String>> nodes = newHashMap();

    private final List<String> connections = newArrayList();

    private final GraphOperations operations;

    public GraphOperationsRdfHandler(GraphOperations operations) {
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

        handleTriplet((URI) subjectResource, predicate, objectValue);
    }

    private void handleTriplet(URI subject, URI predicate, Value object) {
        create(subject);

        if (isType(predicate)) {
            handleType(subject, object);

        } else if (object instanceof Literal) {
            operations.setNodeProperty(subject, nameFor(predicate), (Literal) object);

        } else {
            handleResource(subject, predicate, (URI) object);
        }
    }

    private void handleResource(URI subject, URI predicate, URI object) {
        create(object);

        if (!connectionExists(subject, predicate, object)) {
            operations.connectNodes(subject, predicate, object, nameFor(predicate));
            flagConnectionExisting(subject, predicate, object);
        }
    }

    private void handleType(URI subject, Value object) {
        String label = nameFor((URI) object);

        if (!nodeLabelExists(subject, label)) {
            operations.setNodeLabel(subject, label);
            flagNodeLabelExisting(subject, label);
        }
    }

    private void create(URI node) {
        if (!nodeExists(node)) {
            operations.createNode(node, nameFor(node));
            flagNodeExisting(node);
        }
    }

    private String cacheKeyFor(URI subject, URI predicate, URI object) {
        return format("(%s)-[%s]->(%s)",
                nameFor(subject), nameFor(predicate), nameFor(object));
    }

    private boolean isType(URI predicate) {
        return predicate.stringValue().equals(TYPE_URI);
    }

    private boolean nodeLabelExists(URI subject, String label) {
        return nodes.get(subject.stringValue()).contains(label);
    }

    private boolean nodeExists(URI node) {
        return nodes.containsKey(node.stringValue());
    }

    private boolean connectionExists(URI subject, URI predicate, Value object) {
        return connections.contains(cacheKeyFor(subject, predicate, (URI) object));
    }

    private void flagNodeExisting(URI node) {
        nodes.put(node.stringValue(), new ArrayList<String>());
    }

    private void flagConnectionExisting(URI subject, URI predicate, Value object) {
        connections.add(cacheKeyFor(subject, predicate, (URI) object));
    }

    private boolean flagNodeLabelExisting(URI node, String label) {
        return nodes.get(node.stringValue()).add(label);
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

}
