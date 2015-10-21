package de.tud.feedback;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Goal;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.event.ChangeRequestedEvent;
import de.tud.feedback.event.SymptomDetectedEvent;
import de.tud.feedback.loop.Planner;
import de.tud.feedback.repository.graph.GoalRepository;
import de.tud.feedback.repository.graph.ObjectiveRepository;
import de.tud.feedback.service.ContextService;
import de.tud.feedback.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class EventBroker {

    @Autowired ContextService contextService;

    @Autowired WorkflowService workflowService;

    @Autowired GoalRepository goalRepository;

    @Autowired ObjectiveRepository objectiveRepository;

    @Autowired Planner planner;

    @HandleAfterCreate
    public void importContextSourcesAfterContextCreation(Context context) {
        contextService.importAllOf(context);
        contextService.beginContextUpdates();
    }

    @HandleAfterDelete
    public void deleteWorkflowGoals(Workflow workflow) {
        goalRepository.delete(workflow.getGoals());
    }

    @HandleAfterDelete
    public void deleteGoalObjectives(Goal goal) {
        objectiveRepository.delete(goal.getObjectives());
    }

    @EventListener
    public void startPlanningOn(ChangeRequestedEvent event) {
        planner.generatePlanFor(event);
    }

    @EventListener
    public void analyzeGoalsOn(SymptomDetectedEvent event) {
        workflowService.analyzeGoalsForWorkflowsWithin(event.context());
    }

}
