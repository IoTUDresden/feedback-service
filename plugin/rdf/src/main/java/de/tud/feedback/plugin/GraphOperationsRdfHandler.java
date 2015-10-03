package de.tud.feedback.plugin;

import com.google.common.base.Strings;
import de.tud.feedback.api.GraphOperations;
import org.openrdf.model.*;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;

class GraphOperationsRdfHandler extends RDFHandlerBase {

    private static final String TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    private final GraphOperations operations;

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
        operations.createNode(uriOf(subject));

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
        operations.createNode(uriOf(object));
        operations.createConnection(uriOf(predicate), nameFor(predicate), uriOf(subject), uriOf(object));
    }

    private void handleType(URI subject, URI predicate, Value object) {
        operations.setAdditionalLabel(uriOf(subject), nameFor((URI) object));
        operations.createConnection(uriOf(predicate), nameFor(predicate), uriOf(subject), uriOf(object));
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
