package de.tud.feedback.event;

import de.tud.feedback.domain.Context;

public class ContextImportsFinishedEvent extends ContextEvent {

    public ContextImportsFinishedEvent(Context context) {
        super(context);
    }

    public static ContextImportsFinishedEvent with(Object source) {
        return new ContextImportsFinishedEvent((Context) source);
    }

}
