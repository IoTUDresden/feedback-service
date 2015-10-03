package de.tud.feedback.repository;

import de.tud.feedback.api.FeedbackPlugin;

public interface PluginRepository {

    FeedbackPlugin findOne(String name);

    void register();

}
