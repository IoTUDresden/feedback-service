package de.tud.feedback.loop.impl;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.domain.Context;
import de.tud.feedback.event.SymptomDetectedEvent;
import de.tud.feedback.graph.SimpleCypherExecutor;
import de.tud.feedback.loop.Monitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RealTimeAgentMonitor implements Monitor {

    private static final long REAL_TIME_STEP_MILLIS = 1000L;

    private FeedbackPlugin plugin;

    private SimpleCypherExecutor executor;

    private ApplicationEventPublisher publisher;

    private TaskExecutor tasks;

    private TaskScheduler scheduler;

    @Override
    public void monitor(Context context) {
        ContextUpdater.Listener listener = () ->
                publisher.publishEvent(SymptomDetectedEvent.on(context));

        plugin.getMonitorAgents().forEach(agent -> {
            ContextUpdater updater = plugin.getContextUpdater(executor);
            updater.workWith(listener);
            updater.workWith(context);
            agent.workWith(updater);
            tasks.execute(agent);
        });

        realTimeIsGoingByFor(listener);
    }

    private void realTimeIsGoingByFor(ContextUpdater.Listener listener) {
        scheduler.scheduleWithFixedDelay(() -> listener.contextUpdated(), REAL_TIME_STEP_MILLIS);
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

    @Autowired
    public void setScheduler(TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

}
