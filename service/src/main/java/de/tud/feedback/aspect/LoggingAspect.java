package de.tud.feedback.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static org.slf4j.LoggerFactory.getLogger;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* *(..)) && @annotation(de.tud.feedback.annotation.LogDuration)")
    public void durationLoggedMethods() {}

    @Pointcut("execution(* *(..)) && @annotation(de.tud.feedback.annotation.LogInvocation)")
    public void invocationLoggedMethods() {}

    @Around("durationLoggedMethods()")
    public Object logDuration(ProceedingJoinPoint point) throws Throwable {
        final String method = methodFrom(point);
        final Logger logger = getLogger(classFrom(point));
        final Long begin = currentTimeMillis();
        final Object result;

        logger.info(format("Starting %s(%s)", method, argumentsFrom(point)));
        result = point.proceed();
        logger.info(format("Finished %s(...) after %sms", method, timeGoneBySince(begin)));

        return result;
    }

    @Before("invocationLoggedMethods()")
    public void logInvocation(JoinPoint point) {
        getLogger(classFrom(point)).info(format("%s(%s)", methodFrom(point), argumentsFrom(point)));
    }

    private String timeGoneBySince(long begin) {
        return String.valueOf(millisecondsSince(begin));
    }

    private long millisecondsSince(long begin) {
        return currentTimeMillis() - begin;
    }

    private String argumentsFrom(JoinPoint point) {
        return Arrays.toString(point.getArgs());
    }

    private String methodFrom(JoinPoint point) {
        return MethodSignature.class.cast(point.getSignature()).getMethod().getName();
    }

    private Class classFrom(JoinPoint point) {
        return MethodSignature.class.cast(point.getSignature()).getDeclaringType();
    }

}
