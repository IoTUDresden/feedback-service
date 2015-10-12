package de.tud.feedback.configuration;

import de.tud.feedback.domain.Workflow;
import de.tud.feedback.repository.graph.WorkflowRepository;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.server.Neo4jServer;
import org.springframework.data.neo4j.server.RemoteServer;
import org.springframework.util.Assert;

import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

@Configuration
@PropertySource("classpath:neo4j.properties")
@EnableNeo4jRepositories(basePackageClasses = WorkflowRepository.class)
public abstract class Neo4jConfiguration extends org.springframework.data.neo4j.config.Neo4jConfiguration {

    static final Logger LOG = LoggerFactory.getLogger(Neo4jConfiguration.class);

    static final String DEFAULT_HOST = "localhost";

    static final int DEFAULT_PORT = 7474;

    @Override
    public SessionFactory getSessionFactory() {
        return new SessionFactory(Workflow.class.getPackage().getName());
    }

    @Configuration
    @Profile({ "!embeddedNeo4j", "production" })
    public static class ExternalNeo4jConfiguration extends Neo4jConfiguration implements EnvironmentAware {

        private String url;

        private String username;

        private String password;

        @Override
        public Neo4jServer neo4jServer() {
            return new RemoteServer(url, username, password);
        }

        @Override
        public void setEnvironment(Environment environment) {
            ArrayList<String> profiles = newArrayList(environment.getActiveProfiles());

            if (!profiles.contains("embeddedNeo4j")) {
                Assert.isTrue(environment.containsProperty("service.knowledge.neo4j.url"));
            }

            url = environment.getProperty("service.knowledge.neo4j.url");
            username = environment.getProperty("service.knowledge.neo4j.username", "");
            password = environment.getProperty("service.knowledge.neo4j.password", "");
        }

    }

    @Configuration
    @Profile({ "embeddedNeo4j", "!production" })
    public static class InternalNeo4jConfiguration extends Neo4jConfiguration implements EnvironmentAware {

        private String binary;

        public InternalNeo4jConfiguration() {
            LOG.warn("Using embedded server");
        }

        @Override
        public Neo4jServer neo4jServer() {
            if (neo4j("status") != 0 && neo4j("start") != 0) {
                throw new RuntimeException("Could not start Neo4j");
            }

            return new RemoteServer(format("http://%s:%s", DEFAULT_HOST, DEFAULT_PORT), "", "");
        }

        private int neo4j(String command) {
            try {
                ProcessBuilder builder = new ProcessBuilder(binary, command);
                Process process = builder.start();
                process.waitFor();
                return process.exitValue();
            } catch (Exception exception) {
                LOG.error("Could not execute Neo4j command");
                throw new RuntimeException(exception);
            }
        }

        @Override
        public void setEnvironment(Environment environment) {
            binary = environment.getRequiredProperty("service.knowledge.neo4j.binary");
        }

    }

}
