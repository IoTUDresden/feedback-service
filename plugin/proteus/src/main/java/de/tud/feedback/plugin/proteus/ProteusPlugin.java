package de.tud.feedback.plugin.proteus;

import de.tud.feedback.api.ComponentProvider;
import de.tud.feedback.api.FeedbackServicePlugin;
import de.tud.feedback.api.context.ContextImportStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@FeedbackServicePlugin("proteus")
@PropertySource("classpath:plugin.yml")
class ProteusPlugin implements ComponentProvider {

    @Autowired
    private ContextImportStrategy contextImportStrategy;

    @Override
    public ContextImportStrategy contextImportStrategy() {
        return contextImportStrategy;
    }

}
