package de.tud.feedback.event;

import de.tud.feedback.domain.Objective;

import static java.lang.String.format;

public class ObjectiveFailedEvent extends LoopEvent {

    private ObjectiveFailedEvent(Object source) {
        super(source);
    }

    public static ObjectiveFailedEvent on(Objective objective) {
        return new ObjectiveFailedEvent(objective);
    }

    public Objective objective() {
        return (Objective) getSource();
    }

    @Override
    public String toString() {
        return format("%s(%s)", getClass().getSimpleName(), objective());
    }

}
