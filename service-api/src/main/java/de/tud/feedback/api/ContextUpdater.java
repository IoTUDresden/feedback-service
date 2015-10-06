package de.tud.feedback.api;

public interface ContextUpdater {

    void update(String itemId, Object state);

    ContextUpdater on(ContextReference context);

}
