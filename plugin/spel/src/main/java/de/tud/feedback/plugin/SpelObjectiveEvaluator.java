package de.tud.feedback.plugin;

import de.tud.feedback.CypherExecutor;
import de.tud.feedback.domain.Objective;
import de.tud.feedback.domain.ObjectiveEvaluationResult;
import de.tud.feedback.loop.ObjectiveEvaluator;
import org.joda.time.DateTime;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Collection;
import java.util.Map;

import static de.tud.feedback.Utils.params;
import static java.lang.String.format;
import static org.joda.time.DateTime.now;

public class SpelObjectiveEvaluator implements ObjectiveEvaluator {

    private final ExpressionParser parser = new SpelExpressionParser();

    private final CypherExecutor executor;

    public SpelObjectiveEvaluator(CypherExecutor executor) {
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
                .setCompensateTo(evaluateCompensationRule(objective, expressionResult))
                .setSatisfiedTo(evaluateSatisfiedRule(objective, expressionResult));
    }

    private boolean evaluateCompensationRule(Objective objective, Map<String, Object> expressionResult) {
        StandardEvaluationContext context = new StandardEvaluationContext(objective);
        registerFunctions(context);
        context.setVariable("now", now());
        context.setVariable("objective", objective);
        context.setVariable("goal", objective.getGoal());
        context.setVariables(expressionResult);
        return parser.parseExpression(objective.getCompensateExpression()).getValue(context, Boolean.class);
    }

    private void registerFunctions(StandardEvaluationContext context)  {
        try {
            context.registerFunction("timeFrom", DateTime.class.getDeclaredMethod("parse",new Class[]{String.class}));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Long evaluateTestNodeId(Objective objective, Map<String, Object> expressionResult) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        registerFunctions(context);
        context.setVariable("now", now());
        context.setVariables(expressionResult);
        return parser.parseExpression(objective.getTestNodeIdExpression()).getValue(context, Long.class);
    }

    public boolean evaluateSatisfiedRule(Objective objective, Map<String, Object> expressionResult) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        registerFunctions(context);
        context.setVariable("now", now());
        context.setVariables(expressionResult);
        return parser.parseExpression(objective.getSatisfiedExpression()).getValue(context, Boolean.class);
    }

}
