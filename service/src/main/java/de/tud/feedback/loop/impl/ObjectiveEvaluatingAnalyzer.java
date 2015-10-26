package de.tud.feedback.loop.impl;

import de.tud.feedback.CypherExecutor;
import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.domain.Goal;
import de.tud.feedback.domain.Objective;
import de.tud.feedback.domain.Objective.State;
import de.tud.feedback.domain.ObjectiveEvaluationResult;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.event.ChangeRequestedEvent;
import de.tud.feedback.event.LoopFinishedEvent;
import de.tud.feedback.event.ObjectiveSatisfiedEvent;
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
        boolean allGoalsHaveBeenSatisfied = workflow.getGoals()
                .stream()
                .filter(this::isSatisfied)
                .allMatch(Goal::hasBeenSatisfied);

        boolean nothingLeft = !workflow.getGoals()
                .stream()
                .filter(goal -> goal.getObjectives()
                        .stream()
                        .anyMatch(o -> (o.getState() == State.COMPENSATION) ||
                                       (o.getState() == State.UNSATISFIED)))
                .findAny()
                .isPresent();

        if (allGoalsHaveBeenSatisfied || nothingLeft) {
            publisher.publishEvent(LoopFinishedEvent.on(workflow));
        }
    }

    private boolean isSatisfied(Goal unsatisfiedGoal) {
        return unsatisfiedGoal.getObjectives().stream()
                .filter(objective -> objective.getState() != State.COMPENSATION)
                .filter(this::isSatisfied)
                .allMatch(Objective::hasBeenSatisfied);
    }

    private boolean isSatisfied(Objective objective) {
        ObjectiveEvaluationResult result = evaluator.evaluate(objective);
        boolean weCantGetNoSatisfaction = !result.hasBeenSatisfied();
        boolean compensationIsRequired = result.shouldBeCompensated();

        if (weCantGetNoSatisfaction && compensationIsRequired) {
            objective.setState(State.COMPENSATION);
            publisher.publishEvent(ChangeRequestedEvent.on(objective, result));
            return false;

        } else if (weCantGetNoSatisfaction) {
            objective.setState(State.UNSATISFIED);
            return false;

        } else {
            objective.setState(State.SATISFIED);
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
