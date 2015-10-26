package de.tud.feedback.event;

import de.tud.feedback.domain.Context;

import static java.lang.String.format;

public class SymptomDetectedEvent extends LoopEvent {

    private SymptomDetectedEvent(Object source) {
        super(source);
    }

    public static SymptomDetectedEvent on(Context context) {
        return new SymptomDetectedEvent(context);
    }

    public Context context() {
        return (Context) getSource();
    }

    @Override
    public String toString() {
        return format("%s(%s)", getClass().getSimpleName(), context());
    }

}
