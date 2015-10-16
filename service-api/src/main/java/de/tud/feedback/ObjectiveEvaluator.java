package de.tud.feedback;

import de.tud.feedback.domain.Objective;
import org.springframework.core.Ordered;
import org.springframework.util.MimeType;

import java.util.Collection;

public interface ObjectiveEvaluator extends Ordered {

    Collection<MimeType> getSupportedMimeTypes();

    boolean evaluate(Objective objective);

}
