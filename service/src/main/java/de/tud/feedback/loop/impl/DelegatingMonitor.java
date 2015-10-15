package de.tud.feedback.loop.impl;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.domain.Context;
import de.tud.feedback.graph.SimpleCypherExecutor;
import de.tud.feedback.loop.Monitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DelegatingMonitor implements Monitor {

    private FeedbackPlugin plugin;

    private SimpleCypherExecutor executor;

    private ApplicationEventPublisher publisher;

    private TaskExecutor tasks;

    @Override
    public void monitor(Context context) {
        plugin.monitorAgents().forEach(agent -> {
            ContextUpdater updater = plugin.contextUpdater(executor);
            updater.setApplicationEventPublisher(publisher);
            updater.workWith(context);
            agent.workWith(updater);
            tasks.execute(agent);
        });
    }

    @Autowired
    public void setTaskExecutor(TaskExecutor tasks) {
        this.tasks = tasks;
    }

    @Autowired
    public void setFeedbackPlugin(FeedbackPlugin plugin) {
        this.plugin = plugin;
    }

    @Autowired
    public void setCypherExecutor(SimpleCypherExecutor executor) {
        this.executor = executor;
    }

    @Autowired
    public void setApplicationEventPublisher(ApplicationEventPublisher delegatePublisher) {
        this.publisher = delegatePublisher;
    }

}
