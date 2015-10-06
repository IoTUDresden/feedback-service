package de.tud.feedback.plugin.proteus;

import de.tud.feedback.plugin.ProteusFeedbackPlugin;
import de.tud.feedback.plugin.factory.DogOntContextUpdaterFactoryBean;
import de.tud.feedback.plugin.factory.OpenHabMonitorAgentFactoryBean;
import de.tud.feedback.plugin.factory.RdfContextImporterFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class ProteusConfiguration {

    @Bean
    RdfContextImporterFactoryBean rdfContextImporterFactoryBean() {
        return RdfContextImporterFactoryBean.build()
                .setNodeLabel(StringUtils.capitalize(ProteusFeedbackPlugin.NAME))
                .setNodeIdentifier("uri");
    }

    @Bean
    OpenHabMonitorAgentFactoryBean openHabMonitorAgentFactoryBean(
            @Value("${openHab.host:localhost}") String host,
            @Value("${openHab.port:8080}") int port,
            @Value("${openHab.delta:0.01}") Double delta
    ) {
        return OpenHabMonitorAgentFactoryBean.build()
                .setNumberStateChangeDelta(delta)
                .setHost(host)
                .setPort(port);
    }

    @Bean
    DogOntContextUpdaterFactoryBean dogOntContextUpdaterFactoryBean() {
        return DogOntContextUpdaterFactoryBean.build();
    }

}
