package de.tud.feedback.loop;

import de.tud.feedback.domain.ContextMismatch;

import java.util.Map;

public interface MismatchProvider {

    ContextMismatch getMismatch(String satisfiedExpression, Map<String, Object> contextVariables);

}
