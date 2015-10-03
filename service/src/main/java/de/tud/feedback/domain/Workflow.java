package de.tud.feedback.domain;

import org.hibernate.validator.constraints.NotBlank;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

@NodeEntity
public class Workflow extends Node {

    @Relationship(type = "instanceOf", direction = Relationship.INCOMING)
    private Set<WorkflowInstance> instances = newHashSet();

    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<WorkflowInstance> getInstances() {
        return instances;
    }

    public void setInstances(Set<WorkflowInstance> instances) {
        try {
            this.instances = checkNotNull(instances);
        } catch (NullPointerException exception) {
            this.instances = newHashSet();
        }
    }

}
