package de.tud.feedback.domain;

import org.hibernate.validator.constraints.NotBlank;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import static java.lang.String.format;

@NodeEntity
public class Command {

    @GraphId
    private Long id;

    @NotBlank
    @Property
    private String target;

    @NotBlank
    @Property
    private Type type;

    @NotBlank
    @Property
    private String name;

    public String getTarget() {
        return target;
    }

    public Command setTargetTo(String target) {
        this.target = target;
        return this;
    }

    public Type getType() {
        return type;
    }

    public Command setTypeTo(Type type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public Command setNameTo(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Command command = (Command) o;

        if (target != null ? !target.equals(command.target) : command.target != null) return false;
        if (type != command.type) return false;
        return !(name != null ? !name.equals(command.name) : command.name != null);

    }

    @Override
    public int hashCode() {
        int result = target != null ? target.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return format("Command(%s, %s)", target, name);
    }

    public enum Type {
        UP, DOWN, ASSIGN
    }

}
