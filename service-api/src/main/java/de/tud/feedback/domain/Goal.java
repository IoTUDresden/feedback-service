package de.tud.feedback.domain;

import org.hibernate.validator.constraints.NotBlank;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

@NodeEntity
public class Goal {

    @GraphId
    private Long id;

    @NotBlank
    private String name;

    @Relationship(type = "hasObjective", direction = Relationship.OUTGOING)
    private Collection<Objective> objectives = newArrayList();

    @Relationship(type = "hasGoal", direction = Relationship.INCOMING)
    private Workflow workflow;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Collection<Objective> getObjectives() {
        return objectives;
    }

    public void setObjectives(Collection<Objective> objectives) {
        try {
            this.objectives = checkNotNull(objectives);
        } catch (NullPointerException exception) {
            this.objectives = newArrayList();
        }
    }

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
        this.workflow = workflow;
    }

    @Override
    public String toString() {
        return format("Goal(%s)", name);
    }

}
