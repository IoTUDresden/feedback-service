package de.tud.feedback;

import de.tud.feedback.domain.Context;
import de.tud.feedback.event.ContextImportsFinishedEvent;
import de.tud.feedback.service.ContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EventBindings {

    private static final String EVENT_CONTAINS_CONTEXT =
            "#root.event.source instanceof T(de.tud.feedback.domain.Context)";

    @Autowired
    private ContextService contexts;

    @Autowired
    private ApplicationEventPublisher publisher;

    @EventListener(condition = EVENT_CONTAINS_CONTEXT)
    public void preProcessContextBeforeCreation(BeforeCreateEvent event) {
        contexts.preProcess((Context) event.getSource());
    }

    @Async
    @EventListener(condition = EVENT_CONTAINS_CONTEXT)
    public void importContextSourcesAfterContextCreation(AfterCreateEvent event) {
        contexts.importFrom((Context) event.getSource());
        publisher.publishEvent(ContextImportsFinishedEvent.with(event.getSource()));
    }

}
