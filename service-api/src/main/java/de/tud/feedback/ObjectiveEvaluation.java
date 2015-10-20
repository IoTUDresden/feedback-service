package de.tud.feedback;

import de.tud.feedback.domain.Objective;
import org.springframework.util.MimeType;

import java.util.Collection;

public interface ObjectiveEvaluation {

    Collection<MimeType> getSupportedMimeTypes();

    ObjectiveEvaluationResult evaluate(Objective objective);

}
