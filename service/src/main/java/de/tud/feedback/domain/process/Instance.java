package de.tud.feedback.domain.process;

import de.tud.feedback.domain.Node;
import de.tud.feedback.domain.Workflow;
import org.hibernate.validator.constraints.NotBlank;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.google.common.base.Preconditions.checkNotNull;

@NodeEntity
public class Instance extends Node {

    @Relationship(type = "instanceOf", direction = Relationship.OUTGOING)
    private Workflow workflow;

    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = checkNotNull(workflow);
    }

}
