package de.tud.feedback.event;

import org.springframework.context.ApplicationEvent;

public abstract class LoopEvent extends ApplicationEvent {

    public LoopEvent(Object source) {
        super(source);
    }

}
