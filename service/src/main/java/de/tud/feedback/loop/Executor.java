package de.tud.feedback.loop;

import de.tud.feedback.domain.Command;

public interface Executor {

    void execute(Command command);

}
