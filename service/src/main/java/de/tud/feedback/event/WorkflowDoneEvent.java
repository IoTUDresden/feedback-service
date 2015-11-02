package de.tud.feedback.event;

import de.tud.feedback.domain.Workflow;
import org.springframework.context.ApplicationEvent;

public class WorkflowDoneEvent extends ApplicationEvent {

    WorkflowDoneEvent(Object source) {
        super(source);
    }

    public static WorkflowDoneEvent on(Workflow workflow) {
        return new WorkflowDoneEvent(workflow);
    }

    public Workflow getWorkflow() {
        return (Workflow) getSource();
    }

}
