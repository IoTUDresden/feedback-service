package de.tud.feedback.configuration;

import de.tud.feedback.repository.index.HistoricalStateRepository;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

@Configuration
@EnableElasticsearchRepositories(
        basePackageClasses = HistoricalStateRepository.class)
public class ElasticsearchConfiguration {

    static final Logger LOG = LoggerFactory.getLogger(ElasticsearchConfiguration.class);

    @Configuration
    @Profile("embeddedElasticsearch")
    public static class EmbeddedElasticsearchConfiguration {

        @Bean(name = "elasticsearchClient")
        public Client embeddedClient() {
            LOG.warn("Using embedded server");
            return nodeBuilder().local(true).node().client();
        }

    }

    @Configuration
    @Profile("!embeddedElasticsearch")
    public static class ExternalElasticsearchConfiguration implements EnvironmentAware {

        @Bean(name = "elasticsearchClient")
        public Client externalClient(
                @Value("${service.knowledge.elasticsearch.host}") String host,
                @Value("${service.knowledge.elasticsearch.port:0}") int port
        ) {
            return new TransportClient().addTransportAddress(new InetSocketTransportAddress(host, port));
        }

        @Override
        public void setEnvironment(Environment env) {
            env.getRequiredProperty("service.knowledge.elasticsearch.host");
            env.getRequiredProperty("service.knowledge.elasticsearch.port");
        }

    }

}
