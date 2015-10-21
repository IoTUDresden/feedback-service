package de.tud.feedback.service.impl;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Goal;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.event.WorkflowSatisfiedEvent;
import de.tud.feedback.loop.Analyzer;
import de.tud.feedback.repository.graph.GoalRepository;
import de.tud.feedback.repository.graph.WorkflowRepository;
import de.tud.feedback.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Maps.newConcurrentMap;

@Service
public class PluginWorkflowService implements WorkflowService {

    private final Map<Workflow, Analyzer> analyzers = newConcurrentMap();

    private WorkflowRepository workflowRepository;

    private GoalRepository goalRepository;

    private Provider<Analyzer> analyzerProvider;

    private ApplicationEventPublisher publisher;

    @Override
    public void analyzeGoalsForWorkflowsWithin(Context context) {
        workflowRepository.findWorkflowsWithin(context).forEach(this::analyzeGoalsFor);
    }

    public void analyzeGoalsFor(Workflow workflow) {
        Collection<Goal> goals = workflow.getGoals();

        if (!workflow.hasBeenSatisfied()) {
            boolean hasBeenSatisfiedNow = analyzerFor(workflow).analyze(goals);

            if (hasBeenSatisfiedNow) {
                publisher.publishEvent(WorkflowSatisfiedEvent.on(workflow));
            }

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
    public void setAnalyzerProvider(Provider<Analyzer> analyzer) {
        this.analyzerProvider = analyzer;
    }

    @Autowired
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

}
