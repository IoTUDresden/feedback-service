package de.tud.feedback.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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
public class LoggableAspect {

    @Pointcut("execution(* *(..)) && @annotation(de.tud.feedback.annotation.Loggable)")
    public void loggableMethods() {}

    @Around("loggableMethods()")
    public Object logInvocation(ProceedingJoinPoint point) throws Throwable {
        final String method = methodFrom(point);
        final Logger logger = getLogger(classFrom(point));
        final Long begin = currentTimeMillis();
        final Object result;


        logger.info(format("Starting %s(%s)", method, argumentsFrom(point)));
        result = point.proceed();
        logger.info(format("Finished %s(...) after %sms", method, timeGoneBySince(begin)));

        return result;
    }

    private String timeGoneBySince(long begin) {
        return String.valueOf(millisecondsSince(begin));
    }

    private long millisecondsSince(long begin) {
        return currentTimeMillis() - begin;
    }

    private String argumentsFrom(ProceedingJoinPoint point) {
        return Arrays.toString(point.getArgs());
    }

    private String methodFrom(ProceedingJoinPoint point) {
        return MethodSignature.class.cast(point.getSignature()).getMethod().getName();
    }

    private Class classFrom(ProceedingJoinPoint point) {
        return MethodSignature.class.cast(point.getSignature()).getDeclaringType();
    }

}
