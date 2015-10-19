package de.tud.feedback.plugin;

import de.tud.feedback.ObjectiveEvaluator;
import de.tud.feedback.domain.Objective;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class DogOntObjectiveEvaluator implements ObjectiveEvaluator {

    @Override
    public boolean evaluate(Objective objective) {
        return false;
    }

    @Override
    public Collection<MimeType> getSupportedMimeTypes() {
        return newArrayList(MimeType.valueOf("application/cypher+dogont"));
    }

}
