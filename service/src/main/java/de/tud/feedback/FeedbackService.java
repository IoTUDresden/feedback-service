package de.tud.feedback;

import de.tud.feedback.event.LoopEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class FeedbackService {

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackService.class);

    @EventListener
    public void log(LoopEvent event) {
        LOG.info(event.toString());
    }

    public static void main(String[] args) {
        SpringApplication.run(FeedbackService.class, args);
    }

}
