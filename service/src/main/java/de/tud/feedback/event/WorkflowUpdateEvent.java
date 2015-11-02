package de.tud.feedback.event;

import de.tud.feedback.domain.Workflow;
import org.springframework.context.ApplicationEvent;

public class WorkflowUpdateEvent extends ApplicationEvent {

    WorkflowUpdateEvent(Object source) {
        super(source);
    }

    public static WorkflowUpdateEvent on(Workflow workflow) {
        return new WorkflowUpdateEvent(workflow);
    }

    public Workflow getWorkflow() {
        return (Workflow) getSource();
    }

}
