package de.tud.feedback.configuration;

import de.tud.feedback.repository.index.HistoricalStateRepository;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

@Configuration
@EnableElasticsearchRepositories(
        basePackageClasses = HistoricalStateRepository.class)
public class ElasticsearchConfiguration implements EnvironmentAware {

    private String host;

    private Integer port = 0;

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchTemplate(client());
    }

    @Bean
    public Client client() {
        if (host != null && port > 0) {
            return new TransportClient()
                    .addTransportAddress(new InetSocketTransportAddress(host, port));
        } else {
            return nodeBuilder().local(true).node().client();
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        host = environment.getProperty("service.knowledge.elasticsearch.host");
        port = Integer.valueOf(environment.getProperty("service.knowledge.elasticsearch.port"));
    }

}
