package de.tud.feedback.loop.impl;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.domain.Context;
import de.tud.feedback.graph.SimpleCypherExecutor;
import de.tud.feedback.loop.Monitor;
import de.tud.feedback.loop.MonitorAgent;
import de.tud.feedback.service.LoopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RealTimeAgentMonitor implements Monitor {

    private static final long REAL_TIME_STEP_MILLIS = 1000L;

    private final Collection<MonitorAgent> agents;

    private final ContextUpdater updater;

    private final TaskExecutor tasks;

    private final TaskScheduler scheduler;

    private final LoopService loopService;

    @Autowired
    public RealTimeAgentMonitor(
            FeedbackPlugin plugin, LoopService loopService, SimpleCypherExecutor executor,
            TaskExecutor tasks, TaskScheduler scheduler) {
        this.tasks = tasks;
        this.scheduler = scheduler;
        this.loopService = loopService;

        this.updater = plugin.getContextUpdater(executor);
        this.agents = plugin.getMonitorAgents();
    }

    @Override
    public void monitor(Context context) {
        ContextUpdater.Listener listener = () ->
                loopService.analyzeGoalsForWorkflowsWithin(context);

        updater.workWith(listener);
        updater.workWith(context);

        agents.forEach(agent -> agent.workWith(updater));
        agents.stream().forEach(tasks::execute);

        realTimeIsGoingByFor(listener);
    }

    private void realTimeIsGoingByFor(ContextUpdater.Listener listener) {
        scheduler.scheduleWithFixedDelay(listener::contextUpdated, REAL_TIME_STEP_MILLIS);
    }

}
