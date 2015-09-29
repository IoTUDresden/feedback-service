package de.tud.feedback.configuration;

import de.tud.feedback.domain.Context;
import de.tud.feedback.service.ContextService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class ServiceConfiguration {

    @Bean
    ApplicationListener<AfterCreateEvent> importContextAfterCreateEvent(ContextService service) {
        return new ApplicationListener<AfterCreateEvent>() {
            @Override
            public void onApplicationEvent(AfterCreateEvent event) {
                if (event.getSource() instanceof Context)
                    service.importContext((Context) event.getSource());
            }
        };
    }

}
