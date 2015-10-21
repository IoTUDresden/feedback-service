package de.tud.feedback.domain;

public class ContextMismatch {

    private Object target;

    private Object source;

    private Type type = Type.UNEQUAL;

    public Object getTarget() {
        return target;
    }

    public ContextMismatch setTarget(Object target) {
        this.target = target;
        return this;
    }

    public Object getSource() {
        return source;
    }

    public ContextMismatch setSource(Object source) {
        this.source = source;
        return this;
    }

    public Type getType() {
        return type;
    }

    public ContextMismatch setType(Type type) {
        this.type = type;
        return this;
    }

    public enum Type {
        TOO_LOW, TOO_HIGH, UNEQUAL
    }

}
