package de.tud.feedback.plugin.proteus;

import com.google.common.base.Optional;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.springframework.data.neo4j.template.Neo4jOperations;

class Neo4jRdfHandler extends RDFHandlerBase {

    private static final String TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    public static final int COMMIT_OPERATIONS_THRESHOLD = 150000;

    public static final int COMMIT_MILLISECONDS_THRESHOLD = 30000;

    private GraphDatabaseService service; // TODO DELETE!!!

    private final Neo4jOperations operations;

    private Transaction transaction;

    private Index<Node> index;

    private int totalNodes;

    private int lastCommitNodes;

    private long lastCommitTime;

    public Neo4jRdfHandler(Neo4jOperations operations) {
        this.operations = operations;

        transaction = service.beginTx();
        index = service.index().forNodes("ttlIndex");
    }

    @Override
    public void handleStatement(Statement statement) throws RDFHandlerException {
        try {
            final Node subject = getNodeFrom(statement.getSubject().stringValue());
            final URI predicate = statement.getPredicate();
            final Value object = statement.getObject();

            if (predicate.stringValue().equals(TYPE_URI)) {
                createType(subject, object);

            } else if (object instanceof Literal) {
                createProperty(subject, predicate, object);

            } else {
                createRelationship(subject, predicate, object);
            }

            commitIfNecessary();

        } catch (Exception exception) {
            transaction.failure();
            throw new RDFHandlerException(exception);
        }
    }

    private void createRelationship(Node subject, URI predicate, Value object) {
        final Node objectNode = getNodeFrom(object.stringValue());

        if (!relationExists(subject, predicate.getLocalName(), objectNode)) {
            Relationship relationship = subject.createRelationshipTo(
                    objectNode, DynamicRelationshipType.withName(predicate.getLocalName()));

            relationship.setProperty("__URI__", predicate.stringValue());
        }
    }

    private void createProperty(Node subject, URI predicate, Value object) {
        subject.setProperty(predicate.getLocalName(),
                propertyFor(object, ((Literal) object).getDatatype()));
    }

    private void createType(Node subject, Value object) {
        Iterable<Label> labels = subject.getLabels();
        String label = localNameFor(object);

        if (!containing(labels, label)) {
            subject.addLabel(DynamicLabel.label(label));
        }
    }

    private void commitIfNecessary() {
        long nodeDelta = ++totalNodes - lastCommitNodes;
        long timeDelta = System.currentTimeMillis() - lastCommitTime;

        if (nodeDelta >= COMMIT_OPERATIONS_THRESHOLD ||
                timeDelta >= COMMIT_MILLISECONDS_THRESHOLD) {

            transaction.success();
            transaction = service.beginTx();

            lastCommitNodes = totalNodes;
            lastCommitTime = System.currentTimeMillis();
        }
    }

    private boolean relationExists(Node subject, String predicateName, Node objectNode) {
        final RelationshipType relType = DynamicRelationshipType.withName(predicateName);

        for (Relationship rel : subject.getRelationships(Direction.OUTGOING, relType)) {
            if (rel.getEndNode().equals(objectNode)) {
                return true;
            }
        }

        return false;
    }

    private boolean containing(Iterable<Label> labels, String label) {
        for (Label currentLabel : labels) {
            if (label.equals(currentLabel.name())) {
                return true;
            }
        }

        return false;
    }

    private String localNameFor(Value uriValue) {
        return ((URI) uriValue).getLocalName();
    }

    private Node getNodeFrom(String nodeValue) {
        Optional<Node> indexedNode = indexedNode(nodeValue);

        if (indexedNode.isPresent()) {
            return indexedNode.get();

        } else {
            Node node = service.createNode();
            node.setProperty("__URI__", nodeValue);
            index.add(node, "resource", nodeValue);
            return node;
        }
    }

    private Optional<Node> indexedNode(String value) {
        IndexHits<Node> hits = index.get("resource", value);

        if (hits.hasNext()) {
            return Optional.of(hits.next());
        } else {
            return Optional.absent();
        }
    }

    private Object propertyFor(Value object, URI type) {
        if (type == null) {
            return object.stringValue();
        }

        String localName = type.getLocalName();

        if (localName.toLowerCase().contains("integer") || localName.equals("long")) {
            return ((Literal) object).longValue();

        } else if (localName.toLowerCase().contains("short")) {
            return ((Literal) object).shortValue();

        } else if (localName.equals("byte")) {
            return ((Literal) object).byteValue();

        } else if (localName.equals("char")) {
            return ((Literal) object).byteValue();

        } else if (localName.equals("float")) {
            return ((Literal) object).floatValue();

        } else if (localName.equals("double")) {
            return ((Literal) object).doubleValue();

        } else if (localName.equals("boolean")) {
            return ((Literal) object).booleanValue();

        } else {
            return object.stringValue();
        }
    }

}
