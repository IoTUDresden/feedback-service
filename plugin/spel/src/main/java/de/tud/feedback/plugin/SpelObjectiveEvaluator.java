package de.tud.feedback.plugin;

import com.google.common.collect.ImmutableMap;
import de.tud.feedback.CypherExecutor;
import de.tud.feedback.domain.Command;
import de.tud.feedback.domain.Objective;
import de.tud.feedback.domain.ObjectiveEvaluationResult;
import de.tud.feedback.loop.ObjectiveEvaluator;
import org.joda.time.DateTimeComparator;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

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
        Map<String, String> tmpParams = objective.getGoal().getParameters();
        ImmutableMap.Builder<String, Object> builder = params();
        if(tmpParams != null)
            builder.putAll(tmpParams);

        Collection<Map<String, Object>> result = executor.execute(objective.getContextExpression(), builder.build());

        if (result.size() != 1) {
            throw new RuntimeException(format(
                    "Context expression for %s invalid. Expecting one row.", objective));
        }

        Map<String, Object> expressionResult = result.stream().findFirst().get();

        return ObjectiveEvaluationResult.build()
                .setContextVariables(expressionResult)
                .setTestNodeIdTo(evaluateTestNodeId(objective, expressionResult))
                .setCompensateTo(evaluateCompensationRule(objective, expressionResult))
                .setSatisfiedTo(evaluateSatisfiedRule(objective, expressionResult));
    }

    private boolean evaluateCompensationRule(Objective objective, Map<String, Object> expressionResult) {
        StandardEvaluationContext context = new StandardEvaluationContext(objective);
        context.setVariable("now", now());
        context.setVariable("objective", objective);
        context.setVariable("goal", objective.getGoal());
        context.setVariables(expressionResult);
        context.registerFunction("lastCommandSendBefore", getLastCommandSendBeforeMethod());
        context.registerFunction("robotReachedPosition", getRobotPositionMethod());
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
        context.registerFunction("robotReachedPosition", getRobotPositionMethod());
        return parser.parseExpression(objective.getSatisfiedExpression()).getValue(context, Boolean.class);
    }

    //this is used by the evaluation context via reflection
    @SuppressWarnings("unused")
    private static boolean lastCommandSendBefore(Objective objective, int seconds){
        if(objective.getCommands().isEmpty()) return true;

        final Comparator<Command> comparator = (c1, c2) -> DateTimeComparator.getInstance().compare(c1.getLastSendAt(), c2.getLastSendAt());
        Optional<Command> lastCommand = objective.getCommands().stream().max(comparator);

        if(!lastCommand.isPresent()) return true;

        return now().minusSeconds(seconds).isAfter(lastCommand.get().getLastSendAt());
    }

    private Method getLastCommandSendBeforeMethod(){
        try {
            return getClass().getDeclaredMethod("lastCommandSendBefore", Objective.class, int.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("function for evaluation context could not be found", e);
        }
    }

    private Method getRobotPositionMethod(){
        try {
            return getClass().getDeclaredMethod("robotReachedPosition", new Class[] {String.class, Double.class, Double.class, Double.class});
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("function for evaluation context could not be found", e);
        }
    }

    //this is used by the evaluation context via reflection
    @SuppressWarnings("unused")
    private static boolean robotReachedPosition(String position, Double x, Double y, Double precision){
        if(position == null || position.trim().isEmpty())
            return false;
        position = position.trim();

        LocationUtil util = new LocationUtil(position);
        LocationUtil.Position parsedPosition = util.getPosition();
        if(parsedPosition == null)
            throw new RuntimeException("Failed to parse Robot Position: can only handle P: n.nn n.nn n.nn O: n.nn n.nn n.nn n.nn");

        return parsedPosition.isInRangeOf(x.doubleValue(), y.doubleValue(), precision.doubleValue());
    }

}
