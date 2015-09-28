package de.tud.feedback;

import de.tud.feedback.api.ComponentProvider;
import de.tud.feedback.api.FeedbackServicePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.neo4j.template.Neo4jOperations;

import java.util.Collection;
import java.util.Map;

import static java.lang.String.format;

@SpringBootApplication
public class FeedbackServiceApplication implements ApplicationListener<ContextRefreshedEvent>, CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackServiceApplication.class);

    private Map<String, ComponentProvider> plugins;

    private ApplicationContext context;

    private void refreshPlugins() {
        plugins.clear();

        for (Object bean : beansWithPluginAnnotation()) {
            registerPluginBy(pluginAnnotationFor(bean));
        }
    }

    private void registerPluginBy(FeedbackServicePlugin plugin) {
        plugins.put(plugin.name(), context.getBean(plugin.componentsProvidedBy()));
        LOG.info(format("Feedback plugin '%s' registered", plugin.name()));
    }

    private FeedbackServicePlugin pluginAnnotationFor(Object bean) {
        return AnnotationUtils.findAnnotation(bean.getClass(), FeedbackServicePlugin.class);
    }

    private Collection<Object> beansWithPluginAnnotation() {
        return context.getBeansWithAnnotation(FeedbackServicePlugin.class).values();
    }

    @Autowired
    public void setPlugins(Map<String, ComponentProvider> plugins) {
        this.plugins = plugins;
    }

    @Autowired
    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext() == context) {
            refreshPlugins();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(FeedbackServiceApplication.class, args);
    }

    @Autowired
    Neo4jOperations operations;

    @Autowired
    ComponentProvider provider;

    @Override
    public void run(String... args) throws Exception {
        provider.contextImportStrategy().importContextWith(
                operations::query, "http://elite.polito.it/ontologies/dogont.owl");
    }

}
