package de.tud.feedback.event;

import de.tud.feedback.domain.Context;

public class ContextEvent extends ServiceEvent<Context> {

    public ContextEvent(Context context) {
        super(context);
    }

}
