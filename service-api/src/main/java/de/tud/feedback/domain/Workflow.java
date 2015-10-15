package de.tud.feedback.domain;

import org.hibernate.validator.constraints.NotBlank;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Collection;

import static java.lang.String.format;

@NodeEntity
public class Workflow {

    @GraphId
    private Long id;

    @NotBlank
    private String name;

    @Relationship(type = "hasGoal", direction = Relationship.OUTGOING)
    private Collection<Goal> goals;

    @Relationship(type = "runsWithin", direction = Relationship.OUTGOING)
    private Context context;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Goal> getGoals() {
        return goals;
    }

    public void setGoals(Collection<Goal> goals) {
        this.goals = goals;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return format("Workflow(%s)", name);
    }

}
