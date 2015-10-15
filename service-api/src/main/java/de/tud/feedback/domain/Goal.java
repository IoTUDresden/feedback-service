package de.tud.feedback.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.joda.time.DateTime.now;

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

    @Convert(graphPropertyType = String.class)
    private DateTime created = now();

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

    @JsonIgnore
    public Objective objective(String name) {
        return objectives.stream()
                .filter(objective -> name.equals(objective.getName()))
                .findAny()
                .get();
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

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return format("Goal(%s)", name);
    }

}
