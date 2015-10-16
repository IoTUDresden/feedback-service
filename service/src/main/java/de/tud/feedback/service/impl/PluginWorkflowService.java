package de.tud.feedback.service.impl;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Goal;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.event.WorkflowSatisfiedEvent;
import de.tud.feedback.loop.Analyzer;
import de.tud.feedback.repository.graph.GoalRepository;
import de.tud.feedback.repository.graph.ObjectiveRepository;
import de.tud.feedback.repository.graph.WorkflowRepository;
import de.tud.feedback.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newConcurrentMap;
import static java.util.stream.Collectors.toList;

@Service
public class PluginWorkflowService implements WorkflowService {

    private final Map<Workflow, Analyzer> analyzers = newConcurrentMap();

    private static final int OBJECTIVES_DEPTH = 2;

    private ObjectiveRepository objectiveRepository;

    private WorkflowRepository workflowRepository;

    private GoalRepository goalRepository;

    private Provider<Analyzer> analyzerProvider;

    private ApplicationEventPublisher publisher;

    @Override
    public void analyzeGoalsForWorkflowsWithin(Context context) {
        newArrayList(workflowRepository.findAll(OBJECTIVES_DEPTH)).stream()
                .filter(workflow -> workflow.getContext() == context)
                .forEach(this::analyzeGoalsFor);
    }

    @Override
    public void analyzeGoalsFor(Workflow workflow) {
        Collection<Goal> goals = workflow.getGoals();

        goals.forEach( // FIXME objectives are incomplete somehow => fetch manually
                goal -> goal.setObjectives(newArrayList(objectiveRepository.findAll()).stream().filter(
                        objective -> objective.getGoal().equals(goal)).collect(toList())));

        if (!workflow.hasBeenSatisfied()) {
            boolean hasBeenSatisfiedNow = analyzerFor(workflow).evaluate(goals);

            if (hasBeenSatisfiedNow) {
                publisher.publishEvent(WorkflowSatisfiedEvent.on(workflow));
            }

            goalRepository.save(goals);
        }
    }

    private Analyzer analyzerFor(Workflow workflow) {
        if (!analyzers.containsKey(workflow)) { // TODO delete sometimes
            analyzers.put(workflow, analyzerProvider.get());
        }

        return analyzers.get(workflow);
    }

    @Override
    public void deleteGoalsFor(Workflow workflow) {
        workflow.getGoals().stream().forEach(this::deleteGoal);
    }

    private void deleteGoal(Goal goal) {
        goal.getObjectives().stream().forEach(objectiveRepository::delete);
        goalRepository.delete(goal);
    }

    @Autowired
    public void setWorkflowRepository(WorkflowRepository workflows) {
        this.workflowRepository = workflows;
    }

    @Autowired
    public void setObjectiveRepository(ObjectiveRepository objectives) {
        this.objectiveRepository = objectives;
    }

    @Autowired
    public void setGoalRepository(GoalRepository goals) {
        this.goalRepository = goals;
    }

    @Autowired
    public void setAnalyzerProvider(Provider<Analyzer> analyzer) {
        this.analyzerProvider = analyzer;
    }

    @Autowired
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

}
