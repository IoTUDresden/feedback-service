package de.tud.feedback.event;

import org.springframework.context.ApplicationEvent;

public abstract class ServiceEvent<T> extends ApplicationEvent {

    public ServiceEvent(T source) {
        super(source);
    }

    @Override
    public T getSource() {
        //noinspection unchecked
        return (T) super.getSource();
    }

}
