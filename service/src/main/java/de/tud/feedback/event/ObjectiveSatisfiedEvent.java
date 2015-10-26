package de.tud.feedback.event;

import de.tud.feedback.domain.Objective;

import static java.lang.String.format;

public class ObjectiveSatisfiedEvent extends LoopEvent {

    private ObjectiveSatisfiedEvent(Object source) {
        super(source);
    }

    public static ObjectiveSatisfiedEvent on(Objective objective) {
        return new ObjectiveSatisfiedEvent(objective);
    }

    public Objective objective() {
        return (Objective) getSource();
    }

    @Override
    public String toString() {
        return format("%s(%s)", getClass().getSimpleName(), objective());
    }

}
