package de.tud.feedback;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.WorkflowInstance;
import de.tud.feedback.service.ContextService;
import de.tud.feedback.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.stereotype.Component;

@Component
public class EventBindings {

    @Autowired ContextService contexts;

    @Autowired WorkflowService workflows;

    @EventListener(condition = "#root.event.source instanceof T(de.tud.feedback.domain.Context)")
    public void importContextSourcesAfterContextCreation(AfterCreateEvent event) {
        contexts.importAllOf((Context) event.getSource());
        contexts.beginContextUpdates();
    }

    @EventListener(condition = "#root.event.source instanceof T(de.tud.feedback.domain.WorkflowInstance)")
    public void attendWorkflowExecutionWithNewInstance(AfterCreateEvent event) {
        workflows.attendExecutionOf((WorkflowInstance) event.getSource());
    }

}
