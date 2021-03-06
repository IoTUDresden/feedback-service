package de.tud.feedback.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class ContextNode {

    @GraphId
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static ContextNode fromId(Long id) {
        ContextNode node = new ContextNode();
        node.setId(id);
        return node;
    }

}
