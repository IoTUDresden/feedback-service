package de.tud.feedback;

import de.tud.feedback.domain.Objective;
import de.tud.feedback.domain.ObjectiveEvaluationResult;

public interface ObjectiveEvaluator {

    ObjectiveEvaluationResult evaluate(Objective objective);

}
