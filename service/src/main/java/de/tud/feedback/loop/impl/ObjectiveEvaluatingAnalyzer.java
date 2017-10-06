package de.tud.feedback.loop.impl;

import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.domain.*;
import de.tud.feedback.domain.Objective.State;
import de.tud.feedback.graph.SimpleCypherExecutor;
import de.tud.feedback.loop.Analyzer;
import de.tud.feedback.loop.ObjectiveEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@Scope("prototype")
public class ObjectiveEvaluatingAnalyzer implements Analyzer {

    private final ObjectiveEvaluator evaluator;

    @Autowired
    public ObjectiveEvaluatingAnalyzer(FeedbackPlugin plugin, SimpleCypherExecutor executor) {
        this.evaluator = plugin.getObjectiveEvaluator(executor);
    }

    @Override
    public Optional<ChangeRequest> analyze(Workflow workflow) {
        boolean allGoalsHaveBeenSatisfied = workflow.getGoals()
                .stream()
                .allMatch(Goal::hasBeenSatisfied);

        boolean nothingLeft = !workflow.getGoals()
                .stream()
                .anyMatch(goal -> goal.getObjectives()
                        .stream()
                        .anyMatch(o -> o.getState() == State.COMPENSATION ||
                                o.getState() == State.UNSATISFIED));

        if (allGoalsHaveBeenSatisfied || nothingLeft) {
            workflow.setFinished(true);
            return Optional.empty();

        } else {
            return workflow.getGoals()
                    .stream()
                    .flatMap(goal -> goal.getObjectives().stream())
                    .map(this::toChangeRequest)
                    .filter(Objects::nonNull)
                    .findAny();
        }
    }

    private ChangeRequest toChangeRequest(Objective objective) {
        ObjectiveEvaluationResult result = evaluator.evaluate(objective);
        boolean weCantGetNoSatisfaction = !result.hasBeenSatisfied();
        boolean compensationIsRequired = result.shouldBeCompensated();

        if (weCantGetNoSatisfaction && compensationIsRequired) {
            objective.setState(State.COMPENSATION);
            return ChangeRequest.on(objective, result);

        } else if (weCantGetNoSatisfaction) {
            objective.setState(State.UNSATISFIED);
            return null;

        } else {
            objective.setState(State.SATISFIED);
            return null;
        }
    }

}
