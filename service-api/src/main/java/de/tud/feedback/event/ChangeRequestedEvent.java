package de.tud.feedback.event;

import org.springframework.context.ApplicationEvent;

public class ChangeRequestedEvent extends ApplicationEvent {

    private ChangeRequestedEvent(Object source) {
        super(source);
    }

}
