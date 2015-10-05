package de.tud.feedback.loop.impl;

import de.tud.feedback.api.ContextUpdater;
import de.tud.feedback.api.CypherExecutor;
import de.tud.feedback.api.FeedbackPlugin;
import de.tud.feedback.api.MonitorAgent;
import de.tud.feedback.loop.Monitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class AgentMonitor implements Monitor {

    private final FeedbackPlugin plugin;

    private CypherExecutor executor;

    @Autowired
    public AgentMonitor(FeedbackPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void start() {
        final Collection<MonitorAgent> agents = plugin.getMonitorAgents();
        final ContextUpdater updater = plugin.getContextUpdater(executor);

        agents.forEach(agent -> agent.start(updater));
    }

    @Autowired
    public void setExecutor(CypherExecutor executor) {
        this.executor = executor;
    }

}
