package de.tud.feedback.loop.impl;

import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.ObjectiveEvaluation;
import de.tud.feedback.ObjectiveEvaluationResult;
import de.tud.feedback.domain.Goal;
import de.tud.feedback.domain.Objective;
import de.tud.feedback.event.ChangeRequestedEvent;
import de.tud.feedback.loop.Analyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.joda.time.DateTime.now;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DelegatingAnalyzer implements Analyzer {

    private static final Logger LOG = LoggerFactory.getLogger(DelegatingAnalyzer.class);

    private final ExpressionParser parser = new SpelExpressionParser();

    private FeedbackPlugin plugin;

    private List<ObjectiveEvaluation> evaluators;

    private ApplicationEventPublisher publisher;

    @PostConstruct
    public void initialize() {
        evaluators = newArrayList(plugin.getObjectiveEvaluators());
    }

    @Override
    public boolean analyze(Collection<Goal> goals) {
        return goals.stream()
                .filter(goal -> !goal.hasBeenSatisfied())
                .filter(this::evaluate)
                .allMatch(Goal::hasBeenSatisfied);
    }

    private boolean evaluate(Goal goal) {
        return goal.getObjectives().stream()
                .filter(objective -> !objective.hasBeenSatisfied())
                .filter(objective -> objective.getState() != Objective.State.COMPENSATION)
                .filter(this::evaluate)
                .allMatch(Objective::hasBeenSatisfied);
    }

    private boolean evaluate(Objective objective) {
        Optional<ObjectiveEvaluation> compatibleEvaluator = evaluators.stream()
                .filter(evaluator -> evaluator.getSupportedMimeTypes().contains(objective.getMime()))
                .findFirst();

        if (!compatibleEvaluator.isPresent()) {
            LOG.error(format("There's no evaluator for %s", objective));
            objective.setState(Objective.State.FAILED);
            return false;
        }

        return evaluate(objective, compatibleEvaluator.get());
    }

    private boolean evaluate(Objective objective, ObjectiveEvaluation evaluator) {
        ObjectiveEvaluationResult result = evaluator.evaluate(objective);
        boolean weCantGetNoSatisfaction = !result.hasBeenSatisfied();
        boolean isReadyForCompensation = compensateConditionSatisfiedFor(objective);

        if (weCantGetNoSatisfaction && isReadyForCompensation) {
            LOG.debug(format("%s will be compensated", objective));
            objective.setState(Objective.State.COMPENSATION);
            publisher.publishEvent(ChangeRequestedEvent.on(objective, result));
            return true;

        } else if (weCantGetNoSatisfaction) {
            objective.setState(Objective.State.UNSATISFIED);
            return false;

        } else {
            LOG.debug(format("%s has been satisfied", objective));
            objective.setState(Objective.State.SATISFIED);
            return true;
        }
    }

    public boolean compensateConditionSatisfiedFor(Objective objective) {
        final StandardEvaluationContext context = new StandardEvaluationContext(objective);
        context.setVariable("now", now());
        return (boolean) parser.parseExpression(objective.getCompensateCondition()).getValue(context);
    }

    @Autowired
    public void setFeedbackPlugin(FeedbackPlugin plugin) {
        this.plugin = plugin;
    }

    @Autowired
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

}
