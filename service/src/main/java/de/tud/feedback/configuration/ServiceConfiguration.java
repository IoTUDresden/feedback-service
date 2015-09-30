package de.tud.feedback.configuration;

import de.tud.feedback.domain.context.Context;
import de.tud.feedback.service.ContextService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.core.event.RepositoryEvent;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class ServiceConfiguration {

    @Bean
    ApplicationListener<BeforeCreateEvent> beforeCreationOfContext(ContextService service) {
        //noinspection Convert2Lambda SPR-10675
        return new ApplicationListener<BeforeCreateEvent>() {
            @Override public void onApplicationEvent(BeforeCreateEvent event) {
                if (isContextWithin(event))
                    service.preProcess(contextWithin(event));
            }
        };
    }

    @Bean
    ApplicationListener<AfterCreateEvent> afterCreationOfContext(ContextService service) {
        //noinspection Convert2Lambda SPR-10675
        return new ApplicationListener<AfterCreateEvent>() {
            @Override public void onApplicationEvent(AfterCreateEvent event) {
                if (isContextWithin(event))
                    service.importFrom(contextWithin(event));
            }
        };
    }

    private Context contextWithin(RepositoryEvent event) {
        return (Context) event.getSource();
    }

    private boolean isContextWithin(RepositoryEvent event) {
        return event.getSource() instanceof Context;
    }

}
