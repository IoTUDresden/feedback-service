package de.tud.feedback.configuration;

import de.tud.feedback.repository.index.HistoricalStateRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(
        basePackageClasses = HistoricalStateRepository.class)
public class ElasticsearchConfiguration {

}
