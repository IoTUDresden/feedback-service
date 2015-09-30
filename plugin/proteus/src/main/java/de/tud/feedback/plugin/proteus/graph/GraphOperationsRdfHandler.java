package de.tud.feedback.plugin.proteus.graph;

import com.google.common.base.Strings;
import org.openrdf.model.*;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;

class GraphOperationsRdfHandler extends RDFHandlerBase {

    private static final String TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    private static final URI INDIVIDUAL = new URIImpl("http://www.w3.org/2002/07/owl#namedindividual");

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
        operations.mergeNode(subject, nameFor(subject));

        if (isType(predicate)) {
            handleType(subject, predicate, object);

        } else if (object instanceof Literal) {
            handleProperty(subject, nameFor(predicate), (Literal) object);

        } else {
            handleResource(subject, predicate, (URI) object);
        }
    }

    private void handleProperty(URI node, String name, Literal value) {
        operations.setProperty(node, name, value);
    }

    private void handleResource(URI subject, URI predicate, URI object) {
        operations.mergeNode(object, nameFor(object));
        operations.mergeConnection(subject, predicate, object, nameFor(predicate));
    }

    private void handleType(URI subject, URI predicate, Value object) {
        operations.setLabel(subject, nameFor((URI) object));
        operations.mergeConnection(subject, predicate, object, nameFor(predicate));
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

}
