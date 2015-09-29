package de.tud.feedback.repository;

import de.tud.feedback.api.ComponentProvider;

public interface PluginRepository {


    ComponentProvider findOne(String plugin);

    boolean exists(String plugin);

}
