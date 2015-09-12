package de.tud.feedback.configuration;

import de.tud.feedback.domain.Workflow;
import de.tud.feedback.domain.process.Instance;
import de.tud.feedback.repository.WorkflowRepository;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.server.Neo4jServer;
import org.springframework.data.neo4j.server.RemoteServer;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableNeo4jRepositories(basePackageClasses = {WorkflowRepository.class})
class KnowledgeConfiguration extends Neo4jConfiguration {

    @Bean
    @Override
    public Neo4jServer neo4jServer() {
        return new RemoteServer("http://localhost:7474");
    }

    @Bean
    @Override
    public SessionFactory getSessionFactory() {
        return new SessionFactory(
                Workflow.class.getPackage().getName(),
                Instance.class.getPackage().getName()
        );
    }

    @Bean
    PlatformTransactionManager knowledgeTransactionManager() throws Exception {
        return transactionManager();
    }

}
