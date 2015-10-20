package de.tud.feedback.domain;

public class ContextMismatch {

    private Object targetState;

    private Object actualState;

    private Type type = Type.DIFFERENT;

    public enum Type {
        BELOW, ABOVE, DIFFERENT
    }

    public Object getTargetState() {
        return targetState;
    }

    public void setTargetState(Object targetState) {
        this.targetState = targetState;
    }

    public Object getActualState() {
        return actualState;
    }

    public void setActualState(Object actualState) {
        this.actualState = actualState;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
