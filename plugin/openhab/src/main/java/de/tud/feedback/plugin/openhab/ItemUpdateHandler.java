package de.tud.feedback.plugin.openhab;

import de.tud.feedback.api.ContextUpdater;
import de.tud.feedback.plugin.openhab.domain.OpenHabItem;

public interface ItemUpdateHandler {

    void handle(OpenHabItem item);

    void use(ContextUpdater updater);

}
