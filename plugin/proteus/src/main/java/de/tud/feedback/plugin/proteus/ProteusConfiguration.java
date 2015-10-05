package de.tud.feedback.plugin.proteus;

import de.tud.feedback.plugin.ProteusFeedbackPlugin;
import de.tud.feedback.plugin.factory.DogOntContextUpdaterFactoryBean;
import de.tud.feedback.plugin.factory.OpenHabMonitorAgentFactoryBean;
import de.tud.feedback.plugin.factory.RdfContextImporterFactoryBean;
import de.tud.feedback.plugin.proteus.annotation.ProteusPluginScope;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import static de.tud.feedback.api.impl.ConversationalScope.createScopeConfigurerFor;

@Configuration
public class ProteusConfiguration {

    @Bean
    @ProteusPluginScope
    RdfContextImporterFactoryBean rdfContextImporterFactoryBean() {
        return RdfContextImporterFactoryBean.build()
                .setNodeLabel(StringUtils.capitalize(ProteusFeedbackPlugin.NAME))
                .setNodeIdentifier("uri");
    }

    @Bean
    @ProteusPluginScope
    OpenHabMonitorAgentFactoryBean openHabMonitorAgentFactoryBean() {
        return OpenHabMonitorAgentFactoryBean.build()
                .setHost("localhost")
                .setPort(8080);
    }

    @Bean
    @ProteusPluginScope
    DogOntContextUpdaterFactoryBean dogOntContextUpdaterFactoryBean() {
        return DogOntContextUpdaterFactoryBean.build();
    }

    @Bean
    static CustomScopeConfigurer proteusScopeConfigurer() {
        return createScopeConfigurerFor(ProteusFeedbackPlugin.NAME);
    }

}
