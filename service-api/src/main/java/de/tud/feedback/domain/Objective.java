package de.tud.feedback.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.tud.feedback.Satisfiable;
import org.joda.time.DateTime;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;
import static java.lang.String.join;
import static org.joda.time.DateTime.now;

@NodeEntity
@SuppressWarnings("unused")
public class Objective implements Satisfiable {

    @GraphId
    private Long id;

    @NotNull
    @Property
    private String name;

    @NotNull
    @Property
    private String compensateExpression;

    @NotNull
    @Property
    private String satisfiedExpression;

    @NotNull
    @Property
    private String testNodeIdExpression;

    @NotNull
    @Property
    private String contextExpression;

    @Property
    @Convert(graphPropertyType = String.class)
    private DateTime created = now();

    @JsonIgnore
    @Relationship(type = "executedFor", direction = Relationship.INCOMING)
    private Set<Command> commands = newHashSet();

    @Property
    private State state = State.UNSATISFIED;

    @Relationship(type = "hasObjective", direction = Relationship.INCOMING)
    private Goal goal;

    @JsonIgnore
    public void setCommands(Set<Command> commands) {
        try {
            this.commands = checkNotNull(commands);
        } catch (NullPointerException exception) {
            this.commands = newHashSet();
        }
    }

    public Set<Command> getCommands() {
        return commands;
    }

    public String getTestNodeIdExpression() {
        return testNodeIdExpression;
    }

    public void setTestNodeIdExpression(String testNodeIdExpression) {
        this.testNodeIdExpression = testNodeIdExpression;
    }

    public String getCompensateExpression() {
        return compensateExpression;
    }

    public void setCompensateExpression(String compensateExpression) {
        this.compensateExpression = compensateExpression;
    }

    public String getSatisfiedExpression() {
        return satisfiedExpression;
    }

    public void setSatisfiedExpression(String satisfiedExpression) {
        this.satisfiedExpression = satisfiedExpression;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public State getState() {
        return state;
    }

    @JsonIgnore
    public void setState(State state) {
        this.state = state;
    }

    public String getContextExpression() {
        return contextExpression;
    }

    public void setContextExpression(String expression) {
        this.contextExpression = expression;
    }

    @JsonProperty("contextExpressions")
    public void setContextExpression(Collection<String> contextExpression) {
        this.contextExpression = join(" ", contextExpression);
    }

    public DateTime getCreated() {
        return created;
    }

    @JsonIgnore
    public void setCreated(DateTime created) {
        this.created = created;
    }

    @Override
    public boolean hasBeenSatisfied() {
        return state == State.SATISFIED;
    }

    @Override
    public String toString() {
        return format("Objective(%s)", name);
    }

    public enum State {
        SATISFIED,
        UNSATISFIED,
        COMPENSATION,
        FAILED
    }

}
