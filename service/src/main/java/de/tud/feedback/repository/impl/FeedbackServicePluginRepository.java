package de.tud.feedback.repository.impl;

import de.tud.feedback.api.ComponentProvider;
import de.tud.feedback.api.annotations.FeedbackServicePlugin;
import de.tud.feedback.repository.PluginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.lang.String.format;

@Repository
public class FeedbackServicePluginRepository implements PluginRepository, ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackServicePluginRepository.class);

    private final Map<String, ComponentProvider> plugins = newHashMap();

    private final ApplicationContext context;

    @Autowired
    public FeedbackServicePluginRepository(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public ComponentProvider findOne(String plugin) {
        return plugins.get(plugin);
    }

    @Override
    public boolean exists(String plugin) {
        return plugins.keySet().contains(plugin);
    }

    private void findAllPlugins() {
        plugins.clear();

        for (Object bean : beansWithPluginAnnotation()) {
            registerPluginBy(pluginAnnotationFor(bean));
        }
    }

    private void registerPluginBy(FeedbackServicePlugin plugin) {
        plugins.put(plugin.name(), context.getBean(plugin.componentsProvidedBy()));
        LOG.info(format("Feedback plugin %s registered", plugin.name()));
    }

    private FeedbackServicePlugin pluginAnnotationFor(Object bean) {
        return AnnotationUtils.findAnnotation(bean.getClass(), FeedbackServicePlugin.class);
    }

    private Collection<Object> beansWithPluginAnnotation() {
        return context.getBeansWithAnnotation(FeedbackServicePlugin.class).values();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext() == context) {
            findAllPlugins();
        }
    }

}
