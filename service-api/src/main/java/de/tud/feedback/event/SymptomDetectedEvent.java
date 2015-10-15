package de.tud.feedback.event;

import de.tud.feedback.domain.Context;
import org.springframework.context.ApplicationEvent;

public class SymptomDetectedEvent extends ApplicationEvent {

    private SymptomDetectedEvent(Object source) {
        super(source);
    }

    public static SymptomDetectedEvent on(Context context) {
        return new SymptomDetectedEvent(context);
    }

    public Context context() {
        return (Context) getSource();
    }

}
