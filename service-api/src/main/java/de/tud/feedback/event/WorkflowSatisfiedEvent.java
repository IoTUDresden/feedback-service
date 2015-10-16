package de.tud.feedback.event;

import de.tud.feedback.domain.Workflow;
import org.springframework.context.ApplicationEvent;

public class WorkflowSatisfiedEvent extends ApplicationEvent {

    private WorkflowSatisfiedEvent(Object source) {
        super(source);
    }

    public static WorkflowSatisfiedEvent on(Workflow workflow) {
        return new WorkflowSatisfiedEvent(workflow);
    }

    public Workflow workflow() {
        return (Workflow) getSource();
    }

}
