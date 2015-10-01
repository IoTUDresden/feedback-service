package de.tud.feedback.api.graph;

public interface GraphOperations {

    void mergeNode(String String, String name);

    void mergeConnection(String subject, String predicate, String object, String name);

    void setLabel(String String, String label);

    void setProperty(String String, String name, Object value);

}
