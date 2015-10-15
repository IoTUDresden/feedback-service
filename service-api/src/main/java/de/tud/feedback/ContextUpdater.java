package de.tud.feedback;

import de.tud.feedback.domain.Context;
import org.springframework.context.ApplicationEventPublisherAware;

public interface ContextUpdater extends ApplicationEventPublisherAware {

    void update(String itemId, Object state);

    void workWith(Context context);

}
