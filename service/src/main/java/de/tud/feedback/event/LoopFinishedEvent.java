package de.tud.feedback.event;

import de.tud.feedback.domain.Workflow;

import static java.lang.String.format;

public class LoopFinishedEvent extends LoopEvent {

    private LoopFinishedEvent(Object source) {
        super(source);
    }

    public static LoopFinishedEvent on(Workflow workflow) {
        return new LoopFinishedEvent(workflow);
    }

    public Workflow workflow() {
        return (Workflow) getSource();
    }

    @Override
    public String toString() {
        return format("%s(%s)", getClass().getSimpleName(), workflow());
    }

}
