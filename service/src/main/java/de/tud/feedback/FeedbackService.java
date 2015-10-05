package de.tud.feedback;

import de.tud.feedback.service.ContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;

@EnableAsync
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class FeedbackService {

    @Autowired ContextService contexts;

    @PostConstruct
    public void initialize() {
        contexts.beginUpdatesOnExistingContexts();
    }

    public static void main(String[] args) {
        SpringApplication.run(FeedbackService.class, args);
    }

}
