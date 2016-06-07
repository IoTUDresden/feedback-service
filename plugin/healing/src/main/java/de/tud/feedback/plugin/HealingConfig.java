package de.tud.feedback.plugin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.annotation.EnableJms;

/**
 * Created by Stefan on 10.05.2016.
 */
@Configuration
@EnableJms
@PropertySource("classpath:dmConnection.properties")
public class HealingConfig {
    @Bean
    @Scope("prototype")
    PeerMonitorAgent peerMonitorAgent() {
        return new PeerMonitorAgent();
    }
    @Bean
    @Scope("prototype")
    PeerMetricsMonitorAgent peerMetricsMonitorAgent() {
        return new PeerMetricsMonitorAgent();
    }
}
