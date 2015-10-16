package de.tud.feedback;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.event.SymptomDetectedEvent;
import de.tud.feedback.service.ContextService;
import de.tud.feedback.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class EventBindings {

    @Autowired ContextService contextService;

    @Autowired WorkflowService workflowService;

    @Async
    @HandleAfterCreate
    public void importContextSourcesAfterContextCreation(Context context) {
        contextService.importAllOf(context);
        contextService.beginContextUpdates();
    }

    @HandleAfterCreate
    public void attendWorkflowExecutionWithNewInstance(Workflow workflow) {
        workflowService.analyzeGoalsFor(workflow);
    }

    @HandleAfterDelete
    public void deleteWorkflowGoals(Workflow workflow) {
        workflowService.deleteGoalsFor(workflow);
    }

    @EventListener
    public void analyzeGoalsOn(SymptomDetectedEvent event) {
        workflowService.analyzeGoalsForWorkflowsWithin(event.context());
    }

}
