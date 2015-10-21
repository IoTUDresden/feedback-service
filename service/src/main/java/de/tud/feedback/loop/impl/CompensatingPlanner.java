package de.tud.feedback.loop.impl;

import de.tud.feedback.ChangeRequest;
import de.tud.feedback.loop.Planner;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

@Component
public class CompensatingPlanner implements Planner {

    @Override
    public void generatePlanFor(ChangeRequest changeRequest) {
        String satisfiedExpression = changeRequest.getObjective().getSatisfiedExpression();
        SpelExpressionParser parser = new SpelExpressionParser();
        SpelExpression expression = parser.parseRaw(satisfiedExpression);
    }

}
