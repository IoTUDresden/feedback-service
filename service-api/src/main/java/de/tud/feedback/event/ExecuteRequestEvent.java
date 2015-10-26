package de.tud.feedback.event;

import de.tud.feedback.domain.Command;
import org.springframework.context.ApplicationEvent;

public class ExecuteRequestEvent extends ApplicationEvent {

    public ExecuteRequestEvent(Object command) {
        super(command);
    }

    public static ExecuteRequestEvent on(Command command) {
        return new ExecuteRequestEvent(command);
    }

    public Command command() {
        return (Command) getSource();
    }

}
