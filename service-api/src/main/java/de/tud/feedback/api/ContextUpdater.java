package de.tud.feedback.api;

public interface ContextUpdater {

    void updateContext(Long contextId, String itemId, Object state);

}
