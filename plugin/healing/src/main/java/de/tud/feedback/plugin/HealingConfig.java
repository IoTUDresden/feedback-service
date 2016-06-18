package de.tud.feedback.plugin;

import de.tud.feedback.plugin.factory.HealingCompensationRepositoryFactoryBean;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.Queue;

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
    public Queue queue() {
        return new ActiveMQQueue("server.messages");
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
