package de.tud.feedback.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import static java.lang.String.format;

@NodeEntity
@SuppressWarnings("unused")
public class Command {

    @GraphId
    private Long id;

    @Property
    private String target;

    @Property
    private Type type;

    @Property
    private String name;

    @Property
    private String processName;

    @Property
    private boolean repeatable;

    @Property
    private String commandAddress;

    @Property
    private String commandReceiverId;

    @Relationship(type = "executedFor", direction = Relationship.OUTGOING)
    private Objective objective;

    @JsonIgnore
    public Objective getObjective() {
        return objective;
    }

    public void setObjective(Objective objective) {
        this.objective = objective;
    }

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

    public boolean isRepeatable() {
        return repeatable;
    }

    public Command setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
        return this;
    }

    public Command setCommandAddress(String commandAddress) {
        this.commandAddress = commandAddress;
        return this;
    }

    public String getCommandAddress() {
        return commandAddress;
    }

    public Command setCommandReceiverId(String commandReceiverId) {
        this.commandReceiverId = commandReceiverId;
        return this;
    }

    public String getCommandReceiverId() {
        return commandReceiverId;
    }

    public Command setProcessName(String processName) {
        this.processName = processName;
        return this;
    }

    public String getProcessName() {
        return processName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Command command = (Command) o;

        if (target != null ? !target.equals(command.target) : command.target != null) return false;
        //noinspection SimplifiableIfStatement
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
        UP, DOWN, TAKE, ASSIGN
    }

}
