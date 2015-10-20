package de.tud.feedback.plugin;

import de.tud.feedback.CypherExecutor;
import de.tud.feedback.ObjectiveEvaluator;
import de.tud.feedback.domain.ContextMismatch;
import de.tud.feedback.domain.Objective;
import de.tud.feedback.domain.ObjectiveEvaluationResult;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static org.joda.time.DateTime.now;

public class DogOntObjectiveEvaluator implements ObjectiveEvaluator {

    private final ExpressionParser parser = new SpelExpressionParser();

    private final CypherExecutor executor;

    public DogOntObjectiveEvaluator(CypherExecutor executor) {
        this.executor = executor;
    }

    @Override
    public ObjectiveEvaluationResult evaluate(Objective objective) {
        ObjectiveEvaluationResult result = ObjectiveEvaluationResult.build()
                .setSatisfiedTo(satisfiedStateFor(objective));

        if (!result.hasBeenSatisfied()) {
            result.setCompensateTo(compensateStateFor(objective));
        }

        if (result.shouldBeCompensated()) {
            result.setMismatchesTo(mismatchesFor(objective));
        }

        return result;
    }

    // TODO
    private Collection<ContextMismatch> mismatchesFor(Objective objective) {
        return newArrayList();
    }

    // TODO
    private boolean satisfiedStateFor(Objective objective) {
        return false;
    }

    public boolean compensateStateFor(Objective objective) {
        final StandardEvaluationContext context = new StandardEvaluationContext(objective);

        context.setVariable("now", now());
        context.setVariable("objective", objective);
        context.setVariable("goal", objective.getGoal());

        return parser.parseExpression(objective.getCompensateRule()).getValue(context, Boolean.class);
    }

}
