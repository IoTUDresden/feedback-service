package de.tud.feedback.domain.process;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Instance {

    @GraphId
    private Long id;

    @Relationship(type = "OF", direction = Relationship.OUTGOING)
    private Process process;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
