package de.tud.feedback;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.event.WorkflowUpdateEvent;
import de.tud.feedback.service.ContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

@Controller
@RepositoryEventHandler
public class EventController {

    Logger LOG = LoggerFactory.getLogger(EventController.class);

    private final ContextService contextService;

    private final Map<Long, SseEmitter> sseEmitters = new Hashtable<>();

    @Autowired
    public EventController(ContextService contextService) {
        this.contextService = contextService;
    }

    @RequestMapping("/events/workflows/{workflowId}")
    public SseEmitter subscribeToEventsFor(@PathVariable Long workflowId) {
        // FIXME multiple subscribers
        // if (!sseEmitters.containsKey(workflowId))
        //    sseEmitters.put(workflowId, new SseEmitter());

        SseEmitter emitter = new SseEmitter();
        sseEmitters.put(workflowId, emitter);
        return emitter;
    }

    @EventListener
    public void emitServerSentEventOn(WorkflowUpdateEvent event) throws IOException {
        Workflow workflow = event.getWorkflow();

        // FIXME cyclic serialization reference (multiple ObjectMappers suck)
        workflow.setContext(null);
        workflow.setGoals(null);

        try {
            sseEmitters.get(workflow.getId()).send(workflow);
        } catch (RuntimeException exception) {
            LOG.warn("Nobody was listening to the feedback's result of {}", workflow.getName());
        }
    }

    @HandleAfterCreate
    public void importContextSourcesAfterContextCreation(Context context) {
        contextService.importAllOf(context);
    }

}
