package de.tud.feedback.loop.impl;

import de.tud.feedback.CypherExecutor;
import de.tud.feedback.domain.Objective;
import de.tud.feedback.domain.ObjectiveEvaluationResult;
import de.tud.feedback.graph.SimpleCypherExecutor;
import de.tud.feedback.loop.ObjectiveEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

import static de.tud.feedback.Utils.params;
import static java.lang.String.format;
import static org.joda.time.DateTime.now;

@Component// TODO move to API, Provided through plugin
public class SpelBasedObjectiveEvaluator implements ObjectiveEvaluator {

    private final ExpressionParser parser = new SpelExpressionParser();

    private final CypherExecutor executor;

    @Autowired
    public SpelBasedObjectiveEvaluator(SimpleCypherExecutor executor) {
        this.executor = executor;
    }

    @Override
    public ObjectiveEvaluationResult evaluate(Objective objective) {
        Collection<Map<String, Object>> result = executor.execute(objective.getContextExpression(), params().build());

        if (result.size() != 1)
            throw new RuntimeException(format(
                    "Context expression for %s invalid. Expecting one row.", objective));

        Map<String, Object> expressionResult = result.stream().findFirst().get();

        return ObjectiveEvaluationResult.build()
                .setContextVariables(expressionResult)
                .setTestNodeIdTo(evaluateTestNodeId(objective, expressionResult))
                .setCompensateTo(evaluateCompensationRule(objective))
                .setSatisfiedTo(evaluateSatisfiedRule(objective, expressionResult));
    }

    private boolean evaluateCompensationRule(Objective objective) {
        StandardEvaluationContext context = new StandardEvaluationContext(objective);
        context.setVariable("now", now());
        context.setVariable("objective", objective);
        context.setVariable("goal", objective.getGoal());
        return parser.parseExpression(objective.getCompensateExpression()).getValue(context, Boolean.class);
    }

    private Long evaluateTestNodeId(Objective objective, Map<String, Object> expressionResult) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariables(expressionResult);
        return parser.parseExpression(objective.getTestNodeIdExpression()).getValue(context, Long.class);
    }

    public boolean evaluateSatisfiedRule(Objective objective, Map<String, Object> expressionResult) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariables(expressionResult);
        return parser.parseExpression(objective.getSatisfiedExpression()).getValue(context, Boolean.class);
    }

}
