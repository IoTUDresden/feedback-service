package de.tud.feedback.service.impl;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Goal;
import de.tud.feedback.domain.Objective;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.loop.Analyzer;
import de.tud.feedback.repository.graph.CommandRepository;
import de.tud.feedback.repository.graph.GoalRepository;
import de.tud.feedback.repository.graph.ObjectiveRepository;
import de.tud.feedback.repository.graph.WorkflowRepository;
import de.tud.feedback.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newConcurrentMap;
import static com.google.common.collect.Sets.newHashSet;

@Service
public class PluginWorkflowService implements WorkflowService {

    private final Map<Workflow, Analyzer> analyzers = newConcurrentMap();

    private WorkflowRepository workflowRepository;

    private GoalRepository goalRepository;

    private CommandRepository commandRepository;

    private ObjectiveRepository objectiveRepository;

    private Provider<Analyzer> analyzerProvider;

    @Override
    public void finish(Workflow workflow) {
        workflow.setFinished(true);
        workflowRepository.save(workflow);
    }

    @Override
    public void deleteCascade(Workflow workflow) {
        Set<Goal> goals = goalRepository.findGoalsFor(workflow);

        goals.forEach(goal -> {
            Collection<Objective> objectives = goal.getObjectives();

            objectives.forEach(objective -> {
                commandRepository.delete(objective.getCommands());
                objective.setCommands(newHashSet());
            });

            objectiveRepository.delete(objectives);
            goal.setObjectives(newHashSet());
        });

        goalRepository.delete(goals);
        workflow.setGoals(newHashSet());
    }

    @Override
    public void analyzeGoalsForWorkflowsWithin(Context context) {
        workflowRepository.findWorkflowsWithin(context).forEach(this::analyzeGoalsFor);
    }

    public void analyzeGoalsFor(Workflow workflow) {
        Collection<Goal> goals = workflow.getGoals();

        if (!workflow.hasBeenSatisfied() &&
            !workflow.hasBeenFinished()
        ) {
            analyzerFor(workflow).analyze(workflow);
            goalRepository.save(goals);
        }
    }

    private Analyzer analyzerFor(Workflow workflow) {
        if (!analyzers.containsKey(workflow)) {
            analyzers.put(workflow, analyzerProvider.get());
        }

        return analyzers.get(workflow);
    }

    @Autowired
    public void setWorkflowRepository(WorkflowRepository workflows) {
        this.workflowRepository = workflows;
    }

    @Autowired
    public void setGoalRepository(GoalRepository goals) {
        this.goalRepository = goals;
    }

    @Autowired
    public void setCommandRepository(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }

    @Autowired
    public void setObjectiveRepository(ObjectiveRepository objectiveRepository) {
        this.objectiveRepository = objectiveRepository;
    }

    @Autowired
    public void setAnalyzerProvider(Provider<Analyzer> analyzer) {
        this.analyzerProvider = analyzer;
    }

}
