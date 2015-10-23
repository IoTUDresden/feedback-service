package de.tud.feedback.domain;

public class Command {

    private String target;

    // TODO maybe a string for flexibility
    private Type type;

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

    public enum Type {
        UP, DOWN, ASSIGN
    }

}
