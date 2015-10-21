package de.tud.feedback.event;

import de.tud.feedback.domain.Objective;
import de.tud.feedback.domain.ObjectiveEvaluationResult;
import de.tud.feedback.loop.ChangeRequest;
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
