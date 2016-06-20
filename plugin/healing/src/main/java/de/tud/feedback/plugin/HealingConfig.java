package de.tud.feedback.plugin;

import de.tud.feedback.plugin.factory.HealingCompensationRepositoryFactoryBean;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.Queue;
import javax.jms.Topic;

/**
 * Created by Stefan on 10.05.2016.
 */
@Configuration
@EnableJms
@PropertySource("classpath:dmConnection.properties")
public class HealingConfig {

    @Bean
    @Scope("prototype")
    PeerMetricsMonitorAgent peerMetricsMonitorAgent() {
        return new PeerMetricsMonitorAgent();
    }
    @Bean
    @Scope("prototype")
    PeerProcessMonitorAgent peerProcessMonitorAgent() {
        return new PeerProcessMonitorAgent();
    }

    @Bean
    public Topic queue() {
        return new ActiveMQTopic("server.messages");
    }

    @Bean
    @Primary
    @Scope("prototype")
    HealingCommandExecutor healingCommandExecutor() {
        return new HealingCommandExecutor();
    }

    @Bean
    @Primary
    public HealingCompensationRepositoryFactoryBean healingCompensationRepositoryFactoryBean(ResourceLoader loader) {
        return new HealingCompensationRepositoryFactoryBean().setLoader(loader);
    }
}
