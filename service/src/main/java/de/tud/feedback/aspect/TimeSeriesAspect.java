package de.tud.feedback.aspect;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Doubles;
import de.tud.feedback.annotation.LogTimeSeries;
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
import java.nio.charset.Charset;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

@Aspect
@Component
public class TimeSeriesAspect {

    private final ExpressionParser parser = new SpelExpressionParser();

    private final MetricRegistry metrics;

    @Autowired
    public TimeSeriesAspect(MetricRegistry metrics) {
        this.metrics = metrics;
    }

    @Pointcut("execution(* de.tud..*(..)) && @annotation(de.tud.feedback.annotation.LogTimeSeries)")
    public void timeSeriesAnnotatedMethods() {}

    @Before("timeSeriesAnnotatedMethods()")
    public void saveTimedEntry(JoinPoint point) {
        StandardEvaluationContext context = new StandardEvaluationContext(point.getTarget());
        LogTimeSeries annotation = methodFrom(point).getAnnotation(LogTimeSeries.class);
        Object[] arguments = point.getArgs();
        context.setVariable("args", arguments);

        persist(getValue(annotation.context(), context),
                getValue(annotation.item(), context),
                getValue(annotation.state(), context));
    }

    private void persist(String context, String item, String stringState) {
        String metric = MetricRegistry.name("context", context, item);
        Double doubleState = Doubles.tryParse(stringState);

        if (!isRegistered(metric))
            register(metric);

        save(metric, doubleState != null ? doubleState : checksumFor(stringState));
    }

    private Integer checksumFor(String stringState) {
        return Hashing.adler32().hashString(stringState, Charset.defaultCharset()).asInt() % 100;
    }

    private void register(String metric) {
        metrics.register(metric, new ModifiableGauge<>());
    }

    private boolean isRegistered(String metric) {
        return metrics.getGauges().containsKey(metric);
    }

    @SuppressWarnings("unchecked")
    private void save(String metric, Object state) {
        ((ModifiableGauge) metrics.getGauges().get(metric)).setValue(state);
    }

    private String getValue(String expression, StandardEvaluationContext context) {
        return (String) parser.parseExpression(expression).getValue(context);
    }

    private Method methodFrom(JoinPoint point) {
        return MethodSignature.class.cast(point.getSignature()).getMethod();
    }

    private static class ModifiableGauge<T> implements Gauge<T> {

        private T value;

        @Override
        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

    }

}
