package de.tud.feedback;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.repository.graph.WorkflowRepository;
import de.tud.feedback.service.ContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
@RepositoryEventHandler
public class EventController {

    private final ContextService contextService;

    private final WorkflowRepository workflowRepository;

    @Autowired
    public EventController(ContextService contextService, WorkflowRepository workflowRepository) {
        this.contextService = contextService;
        this.workflowRepository = workflowRepository;
    }

    @SubscribeMapping("/workflows/{workflowId}")
    public Workflow getWorkflow(@DestinationVariable Long workflowId) {
        return workflowRepository.findOne(workflowId);
    }

    @HandleAfterCreate
    public void importContextSourcesAfterContextCreation(Context context) {
        contextService.importAllOf(context);
    }

}
