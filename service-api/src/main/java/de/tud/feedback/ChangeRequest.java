package de.tud.feedback;

import de.tud.feedback.domain.Objective;

public interface ChangeRequest {

    ObjectiveEvaluationResult getResult();

    Objective getObjective();

}
