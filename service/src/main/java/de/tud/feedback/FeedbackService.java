package de.tud.feedback;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.Arrays;

import static java.lang.String.format;

@SpringBootApplication
public class FeedbackService implements EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackService.class);

    @Autowired
    public void configureMetrics(MetricRegistry registry) {
        registry.register("memory", new MemoryUsageGaugeSet());
        registry.register("threads", new ThreadStatesGaugeSet());
        registry.register("gc", new GarbageCollectorMetricSet());
    }

    @Override
    public void setEnvironment(Environment environment) {
        LOG.info(format("Active profiles: %s",
                Arrays.toString(environment.getActiveProfiles()).replaceAll("^\\[(.*)\\]$", "$1")));
    }

    public static void main(String[] args) {
        SpringApplication.run(FeedbackService.class, args);
    }

}
