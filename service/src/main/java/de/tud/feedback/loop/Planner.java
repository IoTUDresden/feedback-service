package de.tud.feedback.loop;

import de.tud.feedback.domain.ChangeRequest;
import de.tud.feedback.domain.Command;

public interface Planner {

    java.util.Optional<Command> plan(ChangeRequest changeRequest);

}
