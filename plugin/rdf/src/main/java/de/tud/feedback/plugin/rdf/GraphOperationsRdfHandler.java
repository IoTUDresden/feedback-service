package de.tud.feedback.plugin.rdf;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import de.tud.feedback.GraphOperations;
import org.openrdf.model.*;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

public class GraphOperationsRdfHandler extends RDFHandlerBase {

    private static final String TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    public static final Joiner COMMA_JOINER = Joiner.on(",");

    private final GraphOperations operations;

    private final Set<String> nodesCreated = newHashSet();

    private final Set<String> connectionsCreated = newHashSet();

    public GraphOperationsRdfHandler(GraphOperations operations) {
        this.operations = operations;
    }

    @Override
    public void handleStatement(Statement statement) throws RDFHandlerException {
        Resource subject = statement.getSubject();
        Value object = statement.getObject();

        if (subject instanceof BNode)
            subject = uriOf((BNode) subject);

        if (object instanceof BNode)
            object = uriOf((BNode) object);

        handleTriplet((URI) subject, statement.getPredicate(), object);
    }

    private void handleTriplet(URI subject, URI predicate, Value object) {
        createNode(subject);

        if (isType(predicate)) {
            handleType(subject, predicate, object);

        } else if (object instanceof Literal) {
            handleProperty(subject, nameFor(predicate), (Literal) object);

        } else {
            handleResource(subject, predicate, (URI) object);
        }
    }

    private void handleProperty(URI node, String name, Literal value) {
        operations.setNodeProperty(uriOf(node), name, converted(value));
    }

    private void handleResource(URI subject, URI predicate, URI object) {
        createNode(object);
        createConnection(subject, predicate, object);
    }

    private void handleType(URI subject, URI predicate, Value object) {
        operations.setAdditionalLabel(uriOf(subject), nameFor((URI) object));
        createConnection(subject, predicate, (URI) object);
    }

    private void createNode(URI node) {
        String nodeUri = uriOf(node);

        if (!nodesCreated.contains(nodeUri)) {
            operations.createNode(nodeUri);
            operations.setNodeProperty(nodeUri, "name", nameFor(node));
            nodesCreated.add(nodeUri);
        }
    }

    private void createConnection(URI subject, URI predicate, URI object) {
        String uriOfPredicate = uriOf(predicate);
        String nameForPredicate = nameFor(predicate);
        String uriOfSubject = uriOf(subject);
        String uriOfObject = uriOf(object);

        String connection = COMMA_JOINER.join(new String[] {
                uriOfPredicate, nameForPredicate, uriOfSubject, uriOfObject });

        if (!connectionsCreated.contains(connection)) {
            operations.createConnection(uriOfPredicate, nameForPredicate, uriOfSubject, uriOfObject);
            connectionsCreated.add(connection);
        }
    }

    private boolean isType(URI predicate) {
        return predicate.stringValue().equals(TYPE_URI);
    }

    private String uriOf(Value value) {
        return value.stringValue();
    }

    private URI uriOf(BNode node) {
        return new URIImpl("blank://" + node.getID());
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
