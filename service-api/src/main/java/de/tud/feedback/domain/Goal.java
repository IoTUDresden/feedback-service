package de.tud.feedback.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.tud.feedback.Satisfiable;
import org.apache.commons.collections.map.HashedMap;
import org.joda.time.DateTime;
import org.neo4j.cypher.internal.compiler.v2_0.functions.Str;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import scala.xml.PrettyPrinter;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;
import static org.joda.time.DateTime.now;

@NodeEntity
@SuppressWarnings("unused")
public class Goal implements Satisfiable {

    @GraphId
    private Long id;

    @NotNull
    @Property
    private String name;

    @Property
    @Convert(graphPropertyType = String.class)
    private DateTime created = now();

    @NotNull
    @Relationship(type = "hasObjective", direction = Relationship.OUTGOING)
    private Set<Objective> objectives = newHashSet();

    //neo4j cant store a map property
    @JsonIgnore
    @Relationship(type = "hasParameter", direction = Relationship.OUTGOING)
    private Set<Parameter> parametersList = newHashSet();

    @NotNull
    @Relationship(type = "hasGoal", direction = Relationship.INCOMING)
    private Workflow workflow;

    public Collection<Objective> getObjectives() {
        return objectives;
    }

    public void setObjectives(Set<Objective> objectives) {
        try {
            this.objectives = checkNotNull(objectives);
        } catch (NullPointerException exception) {
            this.objectives = newHashSet();
        }
    }

    public Objective objective(String name) {
        return objectives.stream()
                .filter(objective -> name.equals(objective.getName()))
                .findAny()
                .get();
    }

    public Collection<Parameter> getParametersList(){
        return parametersList;
    }

    public void setParametersList(Set<Parameter> parameters){
        this.parametersList = parameters;
    }

    @JsonProperty("parameters")
    public Map<String, String> getParameters() {
        if(parametersList == null)
            return new HashMap<>();
        return parametersList.stream()
                .filter(p -> p.getName() != null && !p.getName().isEmpty())
                .collect(Collectors.toMap(Parameter::getName, Parameter::getValue));
    }

    @JsonProperty("parameters")
    public void setParameters(Map<String, String> parameters) {
        if(parameters == null || parameters.isEmpty()) return;
        parameters.entrySet().stream()
                .filter(e -> e.getKey() != null && !e.getKey().isEmpty())
                .forEach(entry -> this.parametersList.add(new Parameter(entry.getKey(), entry.getValue())));
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

    @Override
    public boolean hasBeenSatisfied() {
        return objectives.stream().allMatch(Objective::hasBeenSatisfied);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Goal goal = (Goal) o;

        if (id != null ? !id.equals(goal.id) : goal.id != null) return false;
        //noinspection SimplifiableIfStatement
        if (name != null ? !name.equals(goal.name) : goal.name != null) return false;
        return !(created != null ? !created.equals(goal.created) : goal.created != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        return result;
    }

}
