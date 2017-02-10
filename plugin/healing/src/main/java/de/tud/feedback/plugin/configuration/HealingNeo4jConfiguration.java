package de.tud.feedback.plugin.configuration;

import de.tud.feedback.plugin.domain.NeoProcess;
import de.tud.feedback.plugin.repository.NeoPeerRepository;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.server.Neo4jServer;
import org.springframework.data.neo4j.server.RemoteServer;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.transaction.PlatformTransactionManager;

//@Configuration
//@EnableNeo4jRepositories(basePackageClasses = NeoPeerRepository.class)
public class HealingNeo4jConfiguration {
//        extends Neo4jConfiguration implements EnvironmentAware{
//
//    //TODO this way, or move the config to the service impl?
//
//    static final String DEFAULT_URL = "http://127.0.0.1:7474";
//
//    private String url;
//
//    private String username;
//
//    private String password;
//
//
//    @Override
//    public Neo4jServer neo4jServer() {
//        return new RemoteServer(url, username, password);
//    }
//
//    @Override
//    public SessionFactory getSessionFactory() {
//        return new SessionFactory(NeoProcess.class.getPackage().getName());
//    }
//
//    @Override
//    @Qualifier("neo4jTransactionManager")
//    public PlatformTransactionManager transactionManager() throws Exception {
//        return super.transactionManager();
//    }
//
//    @Override
//    public void setEnvironment(Environment env) {
//        url = env.getProperty("service.knowledge.neo4j.url", DEFAULT_URL);
//        username = env.getProperty("service.knowledge.neo4j.username", "");
//        password = env.getProperty("service.knowledge.neo4j.password", "");
//    }



}
