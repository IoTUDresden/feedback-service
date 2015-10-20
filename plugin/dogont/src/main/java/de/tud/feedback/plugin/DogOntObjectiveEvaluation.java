package de.tud.feedback.plugin;

import de.tud.feedback.ObjectiveEvaluation;
import de.tud.feedback.ObjectiveEvaluationResult;
import de.tud.feedback.domain.Objective;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class DogOntObjectiveEvaluation implements ObjectiveEvaluation {

    @Override
    public ObjectiveEvaluationResult evaluate(Objective objective) {
        return null; // TODO
    }

    @Override
    public Collection<MimeType> getSupportedMimeTypes() {
        return newArrayList(MimeType.valueOf("application/cypher+dogont"));
    }

}
