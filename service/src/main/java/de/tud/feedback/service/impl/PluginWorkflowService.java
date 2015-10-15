package de.tud.feedback.service.impl;

import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.domain.Goal;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.repository.graph.GoalRepository;
import de.tud.feedback.repository.graph.ObjectiveRepository;
import de.tud.feedback.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("workflows")
public class PluginWorkflowService implements WorkflowService {

    private FeedbackPlugin plugin;

    private ObjectiveRepository objectives;

    private GoalRepository goals;

    @Override
    public void attend(Workflow workflow) {
        // TODO
    }

    @Override
    public void deleteGoals(Workflow workflow) {
        workflow.getGoals().stream().forEach(this::deleteGoal);
    }

    private void deleteGoal(Goal goal) {
        goal.getObjectives().stream().forEach(objectives::delete);
        goals.delete(goal);
    }

    @Autowired
    public void setObjectives(ObjectiveRepository objectives) {
        this.objectives = objectives;
    }

    @Autowired
    public void setGoals(GoalRepository goals) {
        this.goals = goals;
    }

    @Autowired
    public void setPlugin(FeedbackPlugin plugin) {
        this.plugin = plugin;
    }

}
