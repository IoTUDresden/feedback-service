package de.tud.feedback.plugin.proteus.graph;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

public interface GraphOperations {

    void createNode(URI uri, String name);

    void connectNodes(URI subject, URI predicate, Value object, String name);

    void setNodeLabel(URI uri, String label);

    void setNodeProperty(URI uri, String name, Literal value);

}
