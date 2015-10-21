package de.tud.feedback.loop;

import de.tud.feedback.domain.Objective;
import de.tud.feedback.domain.ObjectiveEvaluationResult;

public interface ChangeRequest {

    ObjectiveEvaluationResult getResult();

    Objective getObjective();

}
