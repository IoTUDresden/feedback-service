package de.tud.feedback.api.annotations;

import de.tud.feedback.api.ComponentProvider;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface FeedbackServicePlugin {

    String name();

    Class<? extends ComponentProvider> componentsProvidedBy();

}
