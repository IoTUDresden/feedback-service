package de.tud.feedback.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@EnableCaching
@Configuration
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ServiceConfiguration {

    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster(TaskExecutor executor) {
        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
        multicaster.setTaskExecutor(executor);
        return multicaster;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        return executor;
    }

    @Bean
    public TaskScheduler taskScheduler(Executor executor) {
        ConcurrentTaskScheduler scheduler = new ConcurrentTaskScheduler();
        scheduler.setConcurrentExecutor(executor);
        return scheduler;
    }

}
