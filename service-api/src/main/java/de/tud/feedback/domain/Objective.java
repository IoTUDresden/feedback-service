package de.tud.feedback.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.tud.feedback.Satisfiable;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.util.MimeType;

import java.util.Collection;

import static java.lang.String.format;
import static java.lang.String.join;
import static org.joda.time.DateTime.now;

@NodeEntity
public class Objective implements Satisfiable {

    @GraphId
    private Long id;

    @NotBlank
    @Property
    private String name;

    @NotBlank
    @Property
    private String expression;

    @Property
    private boolean failed = false;

    @Property
    private boolean compensable = false;

    @JsonIgnore
    @Convert(graphPropertyType = String.class)
    private DateTime satisfaction = now().plusYears(100);

    @NotBlank
    @Convert(graphPropertyType = String.class)
    private MimeType mime;

    @Relationship(type = "hasObjective", direction = Relationship.INCOMING)
    private Goal goal;

    public DateTime getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(DateTime satisfaction) {
        this.satisfaction = satisfaction;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setExpressions(Collection<String> lines) {
        this.expression = join(" ", lines);
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

    @Override
    @JsonProperty("satisfied")
    public boolean hasBeenSatisfied() {
        return now().isAfter(satisfaction);
    }

    public boolean hasFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public boolean isCompensable() {
        return compensable;
    }

    public void setCompensable(boolean compensable) {
        this.compensable = compensable;
    }

    @Override
    public String toString() {
        return format("Objective(%s)", name);
    }

}
