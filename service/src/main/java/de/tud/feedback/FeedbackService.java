package de.tud.feedback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.Arrays;

import static java.lang.String.format;

@SpringBootApplication
public class FeedbackService implements EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackService.class);

    public static void main(String[] args) {
        SpringApplication.run(FeedbackService.class, args);
    }

    @Override
    public void setEnvironment(Environment environment) {
        LOG.info(format("Active profiles: %s",
                Arrays.toString(environment.getActiveProfiles()).replaceAll("^\\[(.*)\\]$", "$1")));
    }

}
