package de.tud.feedback.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.tud.feedback.Satisfiable;
import org.hibernate.validator.constraints.NotBlank;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.util.MimeType;

import java.util.Collection;

import static java.lang.String.format;
import static java.lang.String.join;

@NodeEntity
public class Objective implements Satisfiable {

    @GraphId
    private Long id;

    @NotBlank
    @Property
    private String name;

    @NotBlank
    @Property
    @JsonProperty("compensateIf")
    private String compensateRule;

    @NotBlank
    @Property
    @JsonProperty("satisfiedIf")
    private String satisfiedRule;

    @NotBlank
    @Property
    private String expression;

    @NotBlank
    @Convert(graphPropertyType = String.class)
    private MimeType mime;

    @Property
    private State state = State.UNSATISFIED;

    @Relationship(type = "hasObjective", direction = Relationship.INCOMING)
    private Goal goal;

    public enum State {
        SATISFIED,
        UNSATISFIED,
        COMPENSATION
    }

    public String getCompensateRule() {
        return compensateRule;
    }

    public String getSatisfiedRule() {
        return satisfiedRule;
    }

    public void setCompensateRule(String compensateRule) {
        this.compensateRule = compensateRule;
    }

    public void setCompensateRule(Collection<String> compensateCondition) {
        this.compensateRule = join(" ", compensateCondition);
    }

    public void setSatisfiedRule(String satisfiedRule) {
        this.satisfiedRule = satisfiedRule;
    }

    public void setSatisfiedRule(Collection<String> satisfiedRule) {
        this.satisfiedRule = join(" ", satisfiedRule);
    }

    public void setExpression(Collection<String> expression) {
        this.expression = join(" ", expression);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MimeType getMime() {
        return mime;
    }

    public void setMime(MimeType mime) {
        this.mime = mime;
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

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public boolean hasBeenSatisfied() {
        return state == State.SATISFIED;
    }

    @Override
    public String toString() {
        return format("Objective(%s)", name);
    }

}
