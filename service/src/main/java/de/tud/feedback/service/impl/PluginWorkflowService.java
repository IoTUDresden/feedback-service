package de.tud.feedback.service.impl;

import de.tud.feedback.domain.Workflow;
import de.tud.feedback.domain.WorkflowInstance;
import de.tud.feedback.graph.SimpleCypherExecutor;
import de.tud.feedback.repository.WorkflowRepository;
import de.tud.feedback.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("workflows")
public class PluginWorkflowService implements WorkflowService {

    private WorkflowRepository workflows;

    private SimpleCypherExecutor executor;

    @Override
    public void attendExecutionOf(WorkflowInstance instance) {
        // TODO
    }

    @Override
    @Cacheable("augmentedWorkflows") // TODO
    public String augment(Workflow workflow) {
        return "Sorry, dude...";
    }

    @Autowired
    public void setWorkflowRepository(WorkflowRepository workflows) {
        this.workflows = workflows;
    }

    @Autowired
    public void setExecutorProvider(SimpleCypherExecutor executor) {
        this.executor = executor;
    }

}
