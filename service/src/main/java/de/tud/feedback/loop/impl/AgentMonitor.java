package de.tud.feedback.loop.impl;

import de.tud.feedback.api.ContextUpdater;
import de.tud.feedback.api.CypherExecutor;
import de.tud.feedback.api.FeedbackPlugin;
import de.tud.feedback.api.MonitorAgent;
import de.tud.feedback.loop.Monitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
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
    public void start() {
        final Collection<MonitorAgent> agents = plugin.getMonitorAgents();
        final ContextUpdater updater = plugin.getContextUpdater(executor);

        agents.forEach(agent -> {
            agent.use(updater);
            tasks.execute(agent);
        });
    }

    @Autowired
    public void setExecutor(CypherExecutor executor) {
        this.executor = executor;
    }

}
