package de.tud.feedback.configuration;

import de.tud.feedback.domain.Workflow;
import de.tud.feedback.repository.graph.WorkflowRepository;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.server.Neo4jServer;
import org.springframework.data.neo4j.server.RemoteServer;

@Configuration
@EnableNeo4jRepositories(basePackageClasses = WorkflowRepository.class)
class Neo4jConfiguration extends org.springframework.data.neo4j.config.Neo4jConfiguration implements EnvironmentAware {

    private String url;

    private String username;

    private String password;

    @Override
    public Neo4jServer neo4jServer() {
        return new RemoteServer(url, username, password);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return new SessionFactory(Workflow.class.getPackage().getName());
    }

    @Override
    public void setEnvironment(Environment environment) {
        url = environment.getProperty("service.knowledge.neo4j.url");
        username = environment.getProperty("service.knowledge.neo4j.username");
        password = environment.getProperty("service.knowledge.neo4j.password");
    }

}
