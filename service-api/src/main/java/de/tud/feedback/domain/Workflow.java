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
public class Workflow {

    @GraphId
    private Long id;

    @NotBlank
    private String name;

    @Relationship(type = "hasGoal", direction = Relationship.OUTGOING)
    private Collection<Goal> goals = newArrayList();

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
        try {
            this.goals = checkNotNull(goals);
        } catch (NullPointerException exception) {
            this.goals = newArrayList();
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Workflow workflow = (Workflow) o;

        return !(id != null ? !id.equals(workflow.id) : workflow.id != null) &&
               !(name != null ? !name.equals(workflow.name) : workflow.name != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

}
