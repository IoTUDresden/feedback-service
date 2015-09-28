package de.tud.feedback.configuration;

import de.tud.feedback.api.ComponentProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@EnableAsync
@Configuration
public class ServiceConfiguration {

    @Bean
    Map<String, ComponentProvider> plugins() {
        return newHashMap();
    }

}
