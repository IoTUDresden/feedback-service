package de.tud.feedback.plugin.proteus.graph;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

public interface GraphOperations {

    void mergeNode(URI uri, String name);

    void mergeConnection(URI subject, URI predicate, Value object, String name);

    void setLabel(URI uri, String label);

    void setProperty(URI uri, String name, Literal value);

}
