package de.tud.feedback.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.tud.feedback.Satisfiable;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;

@NodeEntity
@SuppressWarnings("unused")
public class Workflow implements Satisfiable {

    @GraphId
    private Long id;

    @NotNull
    @Property
    private String name;

    @Relationship(type = "hasGoal", direction = Relationship.OUTGOING)
    @JsonManagedReference
    private Set<Goal> goals = newHashSet();

    @Property
    private boolean hasBeenFinished;

    @NotNull
    @Relationship(type = "runsWithin", direction = Relationship.OUTGOING)
    @JsonManagedReference
    private Context context;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Goal> getGoals() {
        return goals;
    }

    public void setGoals(Set<Goal> goals) {
        try {
            this.goals = checkNotNull(goals);
        } catch (NullPointerException exception) {
            this.goals = newHashSet();
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

    @Override
    @JsonProperty("hasBeenSatisfied")
    public boolean hasBeenSatisfied() {
        return goals.stream().allMatch(Goal::hasBeenSatisfied);
    }

    public void setFinished(boolean hasBeenFinished) {
        this.hasBeenFinished = hasBeenFinished;
    }

    @JsonProperty("hasBeenFinished")
    public boolean hasBeenFinished() {
        return hasBeenFinished;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

}
