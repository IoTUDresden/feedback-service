package de.tud.feedback.service.impl;

import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.WorkflowAugmentation;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.domain.WorkflowInstance;
import de.tud.feedback.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("workflows")
public class PluginWorkflowService implements WorkflowService {

    private FeedbackPlugin plugin;

    @Override
    public void attendExecutionOf(WorkflowInstance instance) {
        // TODO
    }

    @Override
    @Cacheable(WorkflowAugmentation.CACHE)
    public String augment(Workflow workflow) {
        return plugin.workflowAugmentation().augment(workflow);
    }

    @Autowired
    public void setPlugin(FeedbackPlugin plugin) {
        this.plugin = plugin;
    }

}
