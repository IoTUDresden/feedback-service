package de.tud.feedback.domain;

import static java.lang.String.format;

public class ChangeRequest {

    private final Objective objective;

    private final ObjectiveEvaluationResult result;

    private ChangeRequest(Objective objective, ObjectiveEvaluationResult result) {
        this.objective = objective;
        this.result = result;
    }

    public static ChangeRequest on(Objective objective, ObjectiveEvaluationResult result) {
        return new ChangeRequest(objective, result);
    }

    public Objective getObjective() {
        return objective;
    }

    public ObjectiveEvaluationResult getResult() {
        return result;
    }

    @Override
    public String toString() {
        return format("%s(%s)", getClass().getSimpleName(), getObjective());
    }

}
