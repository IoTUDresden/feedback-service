package de.tud.feedback;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.WorkflowInstance;
import de.tud.feedback.service.ContextService;
import de.tud.feedback.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class EventBindings {

    @Autowired ContextService contexts;

    @Autowired WorkflowService workflows;

    @HandleAfterCreate
    public void importContextSourcesAfterContextCreation(Context context) {
        contexts.importAllOf(context);
        contexts.beginContextUpdates();
    }

    @HandleAfterCreate
    public void attendWorkflowExecutionWithNewInstance(WorkflowInstance instance) {
        workflows.attendExecutionOf(instance);
    }

}
