package de.tud.feedback.service.impl;

import de.tud.feedback.api.FeedbackPlugin;
import de.tud.feedback.domain.WorkflowInstance;
import de.tud.feedback.graph.SimpleCypherExecutor;
import de.tud.feedback.repository.PluginRepository;
import de.tud.feedback.repository.WorkflowRepository;
import de.tud.feedback.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Provider;

@Service
public class PluginWorkflowService implements WorkflowService {

    private WorkflowRepository workflows;

    private PluginRepository plugins;

    private Provider<SimpleCypherExecutor> executorProvider;

    @Override
    public void attendExecutionOf(WorkflowInstance instance) {
        // TODO
    }

    private FeedbackPlugin pluginFor(WorkflowInstance instance) {
        return plugins.findOne(instance.getWorkflow().getContext().getPlugin());
    }

    @Autowired
    public void setWorkflowRepository(WorkflowRepository workflows) {
        this.workflows = workflows;
    }

    @Autowired
    public void setPluginRepository(PluginRepository plugins) {
        this.plugins = plugins;
    }

    @Autowired
    public void setExecutorProvider(Provider<SimpleCypherExecutor> executorProvider) {
        this.executorProvider = executorProvider;
    }

}
