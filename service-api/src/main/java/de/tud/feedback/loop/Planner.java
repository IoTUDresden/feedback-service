package de.tud.feedback.loop;

import de.tud.feedback.domain.ChangeRequest;
import de.tud.feedback.domain.Command;

import java.util.Optional;

public interface Planner {

    Optional<Command> plan(ChangeRequest changeRequest);

}
