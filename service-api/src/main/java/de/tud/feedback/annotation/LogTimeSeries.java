package de.tud.feedback.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogTimeSeries {

    String context();

    String item() default "#{#args[0]}";

    String state() default "#{#args[1]}";

}
