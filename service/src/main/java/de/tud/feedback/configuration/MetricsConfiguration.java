package de.tud.feedback.configuration;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import org.elasticsearch.metrics.ElasticsearchReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableMetrics
public class MetricsConfiguration extends MetricsConfigurerAdapter implements EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsConfiguration.class);

    String[] elasticsearchNodes;

    @Override
    public void configureReporters(MetricRegistry metricRegistry) {
        if (elasticsearchNodes != null) {
            configureElasticsearchReporter(metricRegistry);
        } else {
            configureJmxReporter(metricRegistry);
        }
    }

    private void configureJmxReporter(MetricRegistry metricRegistry) {
        registerReporter(JmxReporter
                .forRegistry(metricRegistry)
                .build())
                .start();
    }

    private void configureElasticsearchReporter(MetricRegistry metricRegistry) {
        try {
            registerReporter(ElasticsearchReporter
                    .forRegistry(metricRegistry)
                    .hosts(elasticsearchNodes)
                    .index("mapek")
                    .build())
                    .start(10, TimeUnit.SECONDS);

            LOG.info("Metrics will be reported to elasticsearch");

        } catch (IOException exception) {
            LOG.warn("Initialization of elasticsearch reporter failed, {}", exception.getMessage());
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        elasticsearchNodes = environment.getProperty("metrics.reporter.elasticsearch.nodes", "").split(",");
    }

}
