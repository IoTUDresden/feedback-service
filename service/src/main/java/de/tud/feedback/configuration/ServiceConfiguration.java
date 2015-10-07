package de.tud.feedback.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ServiceConfiguration {

    @Bean
    public TaskExecutor taskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

}
