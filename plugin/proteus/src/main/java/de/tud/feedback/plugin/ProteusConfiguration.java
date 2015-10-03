package de.tud.feedback.plugin;

import de.tud.feedback.plugin.annotation.ProteusPluginScope;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static de.tud.feedback.api.ConversationalScope.createScopeConfigurerFor;

@Configuration
public class ProteusConfiguration {

    @Bean
    @ProteusPluginScope
    RdfContextImporterFactoryBean rdfContextImporterFactoryBean() {
        return RdfContextImporterFactoryBean.build()
                .setNodeLabel(ProteusFeedbackPlugin.NAME)
                .setNodeIdentifier("uri");
    }

    @Bean
    static CustomScopeConfigurer proteusScopeConfigurer() {
        return createScopeConfigurerFor(ProteusFeedbackPlugin.NAME);
    }

}
