package de.tud.feedback.api;

public interface GraphOperations {

    void createNode(String id);

    void createConnection(String id, String type, String startId, String endId);

    void setAdditionalLabel(String id, String label);

    void setNodeProperty(String id, String name, Object value);

}
