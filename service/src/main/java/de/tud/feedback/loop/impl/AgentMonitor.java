package de.tud.feedback.loop.impl;

import de.tud.feedback.api.CypherExecutor;
import de.tud.feedback.api.FeedbackPlugin;
import de.tud.feedback.domain.Context;
import de.tud.feedback.loop.Monitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class AgentMonitor implements Monitor {

    private final FeedbackPlugin plugin;

    private CypherExecutor executor;

    private TaskExecutor tasks;

    @Autowired
    public AgentMonitor(FeedbackPlugin plugin, TaskExecutor tasks) {
        this.plugin = plugin;
        this.tasks = tasks;
    }

    @Override
    public void start(Context context) {
        plugin.monitorAgents().forEach(agent -> {
            agent.use(plugin.contextUpdaterFor(context, executor));
            tasks.execute(agent);
        });
    }

    @Autowired
    public void setExecutor(CypherExecutor executor) {
        this.executor = executor;
    }

}
