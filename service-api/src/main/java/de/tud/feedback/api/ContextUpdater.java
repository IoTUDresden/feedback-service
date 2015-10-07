package de.tud.feedback.api;

public interface ContextUpdater {

    void update(String itemId, Object state);

    void workWith(NamedNode context);

}
