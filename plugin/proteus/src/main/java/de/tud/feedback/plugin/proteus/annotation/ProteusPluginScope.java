package de.tud.feedback.plugin.proteus.annotation;

import de.tud.feedback.plugin.ProteusFeedbackPlugin;
import org.springframework.context.annotation.Scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Scope(ProteusFeedbackPlugin.NAME)
public @interface ProteusPluginScope {
}
