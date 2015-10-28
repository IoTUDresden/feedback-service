package de.tud.feedback;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.event.ChangeRequestedEvent;
import de.tud.feedback.event.ExecuteRequestedEvent;
import de.tud.feedback.event.LoopFinishedEvent;
import de.tud.feedback.event.SymptomDetectedEvent;
import de.tud.feedback.loop.Executor;
import de.tud.feedback.loop.Planner;
import de.tud.feedback.repository.graph.CommandRepository;
import de.tud.feedback.repository.graph.GoalRepository;
import de.tud.feedback.repository.graph.ObjectiveRepository;
import de.tud.feedback.service.ContextService;
import de.tud.feedback.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class EventBroker {

    @Autowired ContextService contextService;

    @Autowired WorkflowService workflowService;

    @Autowired GoalRepository goalRepository;

    @Autowired ObjectiveRepository objectiveRepository;

    @Autowired CommandRepository commandRepository;

    @Autowired Planner planner;

    @Autowired Executor executor;

    @EventListener
    public void startPlanningOn(ChangeRequestedEvent event) {
        planner.plan(event);
    }

    @EventListener
    public void analyzeGoalsOn(SymptomDetectedEvent event) {
        workflowService.analyzeGoalsForWorkflowsWithin(event.context());
    }

    @EventListener
    public void executeCommandOn(ExecuteRequestedEvent event) {
        executor.execute(event.command());
    }

    @EventListener
    public void flagWorkflowFinishedOn(LoopFinishedEvent event) {
        workflowService.finish(event.workflow());
    }

    @HandleAfterCreate
    public void importContextSourcesAfterContextCreation(Context context) {
        contextService.importAllOf(context);
    }

    @HandleBeforeDelete
    public void cascadingDelete(Workflow workflow) {
        workflowService.deleteCascade(workflow);
    }

}
