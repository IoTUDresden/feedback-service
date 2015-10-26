package de.tud.feedback.loop.impl;

import de.tud.feedback.CypherExecutor;
import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.domain.Goal;
import de.tud.feedback.domain.Objective;
import de.tud.feedback.domain.ObjectiveEvaluationResult;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.event.ChangeRequestedEvent;
import de.tud.feedback.event.ObjectiveSatisfiedEvent;
import de.tud.feedback.event.WorkflowSatisfiedEvent;
import de.tud.feedback.graph.SimpleCypherExecutor;
import de.tud.feedback.loop.Analyzer;
import de.tud.feedback.loop.ObjectiveEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ObjectiveEvaluatingAnalyzer implements Analyzer {

    private ObjectiveEvaluator evaluator;

    private ApplicationEventPublisher publisher;

    private FeedbackPlugin plugin;

    private CypherExecutor executor;

    @PostConstruct
    public void init() { // FIXME this is crap => encapsulate plugin (configuration)
        evaluator = plugin.getObjectiveEvaluator(executor);
    }

    @Override
    public void analyze(Workflow workflow) {
        boolean allGoalsHaveBeenSatisfied = workflow.getGoals().stream()
                .filter(goal -> !goal.hasBeenSatisfied())
                .filter(unsatisfiedGoal -> unsatisfiedGoal.getObjectives().stream()
                        .filter(objective -> objective.getState() != Objective.State.COMPENSATION)
                        .filter(this::isSatisfied)
                        .allMatch(Objective::hasBeenSatisfied))
                .allMatch(Goal::hasBeenSatisfied);

        if (allGoalsHaveBeenSatisfied) {
            publisher.publishEvent(WorkflowSatisfiedEvent.on(workflow));
        }
    }

    private boolean isSatisfied(Objective objective) {
        ObjectiveEvaluationResult result = evaluator.evaluate(objective);
        boolean weCantGetNoSatisfaction = !result.hasBeenSatisfied();
        boolean compensationIsRequired = result.shouldBeCompensated();

        if (weCantGetNoSatisfaction && compensationIsRequired) {
            objective.setState(Objective.State.COMPENSATION);
            publisher.publishEvent(ChangeRequestedEvent.on(objective, result));
            return false;

        } else if (weCantGetNoSatisfaction) {
            objective.setState(Objective.State.UNSATISFIED);
            return false;

        } else {
            objective.setState(Objective.State.SATISFIED);
            publisher.publishEvent(ObjectiveSatisfiedEvent.on(objective));
            return true;
        }
    }

    @Autowired
    public void setFeedbackPlugin(FeedbackPlugin plugin) {
        this.plugin = plugin;
    }

    @Autowired
    public void setCypherExecutor(SimpleCypherExecutor executor) {
        this.executor = executor;
    }

    @Autowired
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

}
