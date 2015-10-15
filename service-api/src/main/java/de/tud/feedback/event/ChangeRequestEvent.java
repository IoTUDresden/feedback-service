package de.tud.feedback.event;

import org.springframework.context.ApplicationEvent;

public class ChangeRequestEvent extends ApplicationEvent {

    private ChangeRequestEvent(Object source) {
        super(source);
    }

}
