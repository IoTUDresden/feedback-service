package de.tud.feedback.event;

import de.tud.feedback.domain.Workflow;

import static java.lang.String.format;

public class WorkflowSatisfiedEvent extends LoopEvent {

    private WorkflowSatisfiedEvent(Object source) {
        super(source);
    }

    public static WorkflowSatisfiedEvent on(Workflow workflow) {
        return new WorkflowSatisfiedEvent(workflow);
    }

    public Workflow workflow() {
        return (Workflow) getSource();
    }

    @Override
    public String toString() {
        return format("%s(%s)", getClass().getSimpleName(), workflow());
    }

}
