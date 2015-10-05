package de.tud.feedback.repository.impl;

import de.tud.feedback.annotation.LogInvocation;
import de.tud.feedback.api.FeedbackPlugin;
import de.tud.feedback.repository.PluginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@Repository
public class FeedbackServicePluginRepository implements PluginRepository {

    private final Map<String, FeedbackPlugin> plugins = newHashMap();

    private final ApplicationContext context;

    @Autowired
    public FeedbackServicePluginRepository(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public FeedbackPlugin findOne(String name) {
        return plugins.get(name);
    }

    @LogInvocation
    @PostConstruct
    public void initialize() {
        plugins.clear();
        pluginBeans().forEach(this::initialize);
    }

    private void initialize(FeedbackPlugin plugin) {
        plugins.put(plugin.name(), plugin);
    }

    private Collection<FeedbackPlugin> pluginBeans() {
        return context.getBeansOfType(FeedbackPlugin.class).values();
    }

}
