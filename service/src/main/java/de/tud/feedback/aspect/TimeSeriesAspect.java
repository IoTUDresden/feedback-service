package de.tud.feedback.aspect;

import de.tud.feedback.annotation.LogTimeSeries;
import de.tud.feedback.index.HistoricalState;
import de.tud.feedback.repository.index.HistoricalStateRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Calendar;

@Aspect
@Component
public class TimeSeriesAspect {

    private final ExpressionParser parser = new SpelExpressionParser();

    private HistoricalStateRepository repository;

    @Pointcut("execution(* *(..)) && @annotation(de.tud.feedback.annotation.LogTimeSeries)")
    public void timeSeriesAnnotatedMethods() {}

    @Before("timeSeriesAnnotatedMethods()")
    public void saveTimedEntry(JoinPoint point) {
        StandardEvaluationContext context = new StandardEvaluationContext(point.getTarget());
        LogTimeSeries annotation = methodFrom(point).getAnnotation(LogTimeSeries.class);
        Object[] arguments = point.getArgs();
        HistoricalState item = new HistoricalState();

        context.setVariable("args", arguments);

        item.setContext(getValue(annotation.context(), context));
        item.setItem(getValue(annotation.item(), context));
        item.setState(getValue(annotation.state(), context));
        item.setTime(Calendar.getInstance().getTime());

        repository.save(item);
    }

    private String getValue(String expression, StandardEvaluationContext context) {
        return (String) parser.parseExpression(expression).getValue(context);
    }

    private Method methodFrom(JoinPoint point) {
        return MethodSignature.class.cast(point.getSignature()).getMethod();
    }

    @Autowired
    public void setRepository(HistoricalStateRepository repository) {
        this.repository = repository;
    }

}
