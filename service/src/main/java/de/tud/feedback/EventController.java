package de.tud.feedback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.event.WorkflowUpdateEvent;
import de.tud.feedback.service.ContextService;
import org.neo4j.ogm.session.Neo4jSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
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

    private final Neo4jSession neo4j;

    private final Map<Long, SseEmitter> sseEmitters = new Hashtable<>();

    @Autowired
    public EventController(ContextService contextService, Neo4jSession neo4j) {
        this.contextService = contextService;
        this.neo4j = neo4j;
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
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode()
                .put("hasBeenSatisfied", workflow.hasBeenSatisfied())
                .put("hasBeenFinished", workflow.hasBeenFinished());

        try {
            sseEmitters.get(workflow.getId()).send(result);
            LOG.debug("Sent result for '{}': {}", workflow.getName(), result.toString());
        } catch (RuntimeException exception) {
            LOG.warn("Nobody was listening to the result of {}", workflow.getName());
        }
    }

    @HandleAfterCreate
    public void importContextSourcesAfterContextCreation(Context context) {
        contextService.importAllOf(context);
    }

    /**
     * It's a hack due to https://jira.spring.io/browse/DATAGRAPH-918
     */
    @HandleAfterDelete
    public void clearWholeNeo4jMappingContext(Object object) {
        neo4j.context().clear();
    }

}
