package de.tud.feedback;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.event.WorkflowDoneEvent;
import de.tud.feedback.repository.graph.WorkflowRepository;
import de.tud.feedback.service.ContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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

    private final ContextService contextService;

    private final Map<Long, SseEmitter> sseEmitters = new Hashtable<>();

    @Autowired
    public EventController(ContextService contextService) {
        this.contextService = contextService;
    }

    @RequestMapping("/events/workflows/{workflowId}")
    public SseEmitter subscribeToEventsFor(@PathVariable Long workflowId) {
        if (!sseEmitters.containsKey(workflowId))
            sseEmitters.put(workflowId, new SseEmitter());

        return sseEmitters.get(workflowId);
    }

    @EventListener
    public void emitServerSentEventOn(WorkflowDoneEvent event) throws IOException {
        Workflow workflow = event.getWorkflow();
        sseEmitters.get(workflow.getId()).send(workflow);
    }

    @HandleAfterCreate
    public void importContextSourcesAfterContextCreation(Context context) {
        contextService.importAllOf(context);
    }

}
