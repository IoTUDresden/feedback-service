package de.tud.feedback.configuration;

import de.tud.feedback.domain.Workflow;
import de.tud.feedback.domain.WorkflowInstance;
import de.tud.feedback.repository.WorkflowRepository;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.conversion.MetaDataDrivenConversionService;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.server.Neo4jServer;
import org.springframework.data.neo4j.server.RemoteServer;

@Configuration
@EnableNeo4jRepositories(basePackageClasses = {WorkflowRepository.class})
class KnowledgeConfiguration extends Neo4jConfiguration implements EnvironmentAware {

    private String neo4jUrl;

    private String neo4jUsername;

    private String neo4jPassword;

    @Override
    public Neo4jServer neo4jServer() {
        return new RemoteServer(neo4jUrl, neo4jUsername, neo4jPassword);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return new SessionFactory(
                Workflow.class.getPackage().getName(),
                WorkflowInstance.class.getPackage().getName());
    }

    @Bean
    public ConversionService conversionService(
            Converter<String, Resource> stringResourceConverter,
            Converter<Resource, String> resourceStringConverter
    ) {
        GenericConversionService conversionService
                = new MetaDataDrivenConversionService(getSessionFactory().metaData());

        conversionService.addConverter(stringResourceConverter);
        conversionService.addConverter(resourceStringConverter);

        return conversionService;
    }

    @Override
    public void setEnvironment(Environment environment) {
        neo4jUrl = environment.getProperty("service.knowledge.url");
        neo4jUsername = environment.getProperty("service.knowledge.username");
        neo4jPassword = environment.getProperty("service.knowledge.password");
    }

}
