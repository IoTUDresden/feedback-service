package de.tud.feedback.loop.impl;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.domain.Context;
import de.tud.feedback.graph.SimpleCypherExecutor;
import de.tud.feedback.loop.Monitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class AgentMonitor implements Monitor {

    private final FeedbackPlugin plugin;

    private SimpleCypherExecutor executor;

    private TaskExecutor tasks;

    @Autowired
    public AgentMonitor(FeedbackPlugin plugin, TaskExecutor tasks) {
        this.plugin = plugin;
        this.tasks = tasks;
    }

    @Override
    public void start(Context context) {
        plugin.monitorAgents().forEach(agent -> {
            ContextUpdater updater = plugin.contextUpdater(executor);
            updater.workWith(context);
            agent.workWith(updater);
            tasks.execute(agent);
        });
    }

    @Autowired
    public void setExecutor(SimpleCypherExecutor executor) {
        this.executor = executor;
    }

}
