package de.tud.feedback.event;

import de.tud.feedback.domain.Command;

import static java.lang.String.format;

public class ExecuteRequestedEvent extends LoopEvent {

    private ExecuteRequestedEvent(Object command) {
        super(command);
    }

    public static ExecuteRequestedEvent on(Command command) {
        return new ExecuteRequestedEvent(command);
    }

    public Command command() {
        return (Command) getSource();
    }

    @Override
    public String toString() {
        return format("%s(%s)", getClass().getSimpleName(), command());
    }

}
