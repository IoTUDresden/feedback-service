package de.tud.feedback.plugin;

import com.google.common.base.Optional;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import de.tud.feedback.domain.ContextMismatch;
import de.tud.feedback.loop.MismatchProvider;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.*;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

// LATER handle null values
public class SpelMismatchProvider implements MismatchProvider {

    private static final ArrayList<String> SUPPORTED_OPS = newArrayList(">", "<", "==");

    @Override
    public ContextMismatch getMismatch(String expression, Map<String, Object> contextVariables) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        SpelExpressionParser parser = new SpelExpressionParser();

        context.setVariables(contextVariables);
        SpelNode ast = parser.parseRaw(expression).getAST();

        validate(ast, expression);

        return new ContextMismatch()
                .setSource(parser.parseExpression(variableWithin(ast).get().toStringAST()).getValue(context))
                .setTarget(literalTargetFor(ast))
                .setType(mismatchTypeFor(ast));
    }

    private Object literalTargetFor(SpelNode ast) {
        String value = literalWithin(ast).get().getOriginalValue();

        Optional<Integer> integerValue = Optional.fromNullable(Ints.tryParse(value));
        if (integerValue.isPresent()) return integerValue.get();

        Optional<Double> doubleValue = Optional.fromNullable(Doubles.tryParse(value));
        if (doubleValue.isPresent()) return doubleValue.get();

        if (value.matches("^'.*'$")) return value.substring(1, value.length() - 1);

        return value;
    }

    private void validate(SpelNode ast, String expression) {
        if (!operatorWithin(ast).isPresent())
            throw new RuntimeException(format("'%s' is not a binary satisfaction expression", expression));

        if (!variableWithin(ast).isPresent())
            throw new RuntimeException(format("No context variable within '%s'", expression));

        if (!literalWithin(ast).isPresent())
            throw new RuntimeException(format("No value literal within '%s'", expression));

        if (!SUPPORTED_OPS.contains(((Operator) ast).getOperatorName()))
            throw new RuntimeException(format("Operator within '%s' can only be %s", expression, SUPPORTED_OPS));
    }

    private ContextMismatch.Type mismatchTypeFor(SpelNode ast) {
        if ( isEqualityOperatorWithin(ast))                              return ContextMismatch.Type.UNEQUAL;
        if ( variableFirstWithin(ast) && !isLessThanOperatorWithin(ast)) return ContextMismatch.Type.TOO_LOW;
        if (!variableFirstWithin(ast) && !isLessThanOperatorWithin(ast)) return ContextMismatch.Type.TOO_HIGH;
        if ( variableFirstWithin(ast) &&  isLessThanOperatorWithin(ast)) return ContextMismatch.Type.TOO_HIGH;
        if (!variableFirstWithin(ast) &&  isLessThanOperatorWithin(ast)) return ContextMismatch.Type.TOO_LOW;

        return ContextMismatch.Type.UNEQUAL; // covered through validation => never reached
    }

    private boolean isLessThanOperatorWithin(SpelNode ast) {
        return "<".equals(operatorWithin(ast).get());
    }

    private boolean isEqualityOperatorWithin(SpelNode ast) {
        return "==".equals(operatorWithin(ast).get());
    }

    private boolean variableFirstWithin(SpelNode ast) {
        return ast.getChild(0) instanceof VariableReference;
    }

    private Optional<VariableReference> variableWithin(SpelNode ast) {
        if (ast.getChild(0) instanceof VariableReference) {
            return Optional.of((VariableReference) ast.getChild(0));
        } else if (ast.getChild(1) instanceof VariableReference) {
            return Optional.of((VariableReference) ast.getChild(1));
        } else if(ast.getChild(0) instanceof FunctionReference){
            //this allows to use a function with the contextVariable
            //we assume that the first parameter of this function is the context parameter
            FunctionReference ref = (FunctionReference)ast.getChild(0);
            if(ref.getChildCount() == 0 || !(ref.getChild(0) instanceof  VariableReference))
                return Optional.absent();
            return Optional.of((VariableReference) ref.getChild(0));
        } else {
            return Optional.absent();
        }
    }

    private Optional<Literal> literalWithin(SpelNode ast) {
        if (ast.getChild(0) instanceof Literal) {
            return Optional.of((Literal) ast.getChild(0));
        } else if (ast.getChild(1) instanceof Literal) {
            return Optional.of((Literal) ast.getChild(1));
        } else {
            return Optional.absent();
        }
    }

    private Optional<String> operatorWithin(SpelNode ast) {
        boolean isSupportedOperator;

        isSupportedOperator  = ast instanceof OpEQ;
        isSupportedOperator |= ast instanceof OpLT;
        isSupportedOperator |= ast instanceof OpGT;

        if (!isSupportedOperator || ast.getChildCount() != 2) {
            return Optional.absent();
        } else {
            return Optional.of(((Operator) ast).getOperatorName());
        }
    }

}
