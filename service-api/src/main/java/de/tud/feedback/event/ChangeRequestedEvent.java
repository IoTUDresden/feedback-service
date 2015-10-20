package de.tud.feedback.event;

import de.tud.feedback.ChangeRequest;
import de.tud.feedback.ObjectiveEvaluationResult;
import de.tud.feedback.domain.Objective;
import org.springframework.context.ApplicationEvent;

public class ChangeRequestedEvent extends ApplicationEvent implements ChangeRequest {

    private final ObjectiveEvaluationResult result;

    private ChangeRequestedEvent(Objective objective, ObjectiveEvaluationResult result) {
        super(objective);
        this.result = result;
    }

    public static ChangeRequestedEvent on(Objective goal, ObjectiveEvaluationResult result) {
        return new ChangeRequestedEvent(goal, result);
    }

    @Override
    public Objective getObjective() {
        return (Objective) getSource();
    }

    @Override
    public ObjectiveEvaluationResult getResult() {
        return result;
    }

}
