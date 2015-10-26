package de.tud.feedback.loop;

import de.tud.feedback.domain.Command;

public interface CommandExecutor {

    void execute(Command command);

}
