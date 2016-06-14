package de.tud.feedback.loop;

import de.tud.feedback.domain.ChangeRequest;
import de.tud.feedback.domain.Workflow;

import java.util.Optional;

public interface Analyzer {

    Optional<ChangeRequest> analyze(Workflow workflow);

}
