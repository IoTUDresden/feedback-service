package de.tud.feedback.service.impl;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Goal;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.loop.Analyzer;
import de.tud.feedback.repository.graph.GoalRepository;
import de.tud.feedback.repository.graph.ObjectiveRepository;
import de.tud.feedback.repository.graph.WorkflowRepository;
import de.tud.feedback.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newConcurrentMap;

@Service
public class PluginWorkflowService implements WorkflowService {

    private final Map<Workflow, Analyzer> analyzers = newConcurrentMap();

    private ObjectiveRepository objectives;

    private WorkflowRepository workflows;

    private GoalRepository goals;

    private Provider<Analyzer> analyzer;

    @Override
    public void analyzeGoalsForWorkflowsWithin(Context context) {
        newArrayList(workflows.findAll()).stream()
                .filter(workflow -> workflow.getContext() == context)
                .forEach(this::analyzeGoalsFor);
    }

    @Override
    public void analyzeGoalsFor(Workflow workflow) {
        if (!analyzers.containsKey(workflow)) {
            analyzers.put(workflow, analyzer.get());
        }

        analyzers.get(workflow).analyze(workflow.getGoals(), workflow.getContext());
    }

    @Override
    public void deleteGoalsFor(Workflow workflow) {
        workflow.getGoals().stream().forEach(this::deleteGoal);
    }

    private void deleteGoal(Goal goal) {
        goal.getObjectives().stream().forEach(objectives::delete);
        goals.delete(goal);
    }

    @Autowired
    public void setWorkflowRepository(WorkflowRepository workflows) {
        this.workflows = workflows;
    }

    @Autowired
    public void setObjectiveRepository(ObjectiveRepository objectives) {
        this.objectives = objectives;
    }

    @Autowired
    public void setGoalRepository(GoalRepository goals) {
        this.goals = goals;
    }

    @Autowired
    public void setAnalyzerProvider(Provider<Analyzer> analyzer) {
        this.analyzer = analyzer;
    }

}
