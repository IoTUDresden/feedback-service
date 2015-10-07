package de.tud.feedback;

import de.tud.feedback.domain.Context;

public interface ContextUpdater {

    void update(String itemId, Object state);

    void workWith(Context context);

}
