package de.tud.feedback;

import de.tud.feedback.api.ComponentProvider;
import de.tud.feedback.api.context.CypherExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.template.Neo4jOperations;

import java.util.Map;

@SpringBootApplication
public class FeedbackServiceApplication implements CommandLineRunner {

    @Autowired
    Neo4jOperations operations;
    @Autowired
    ComponentProvider provider;

    public static void main(String[] args) {
        SpringApplication.run(FeedbackServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        provider.contextImportStrategy().importContextWith(new CypherExecutor() {
            @Override
            public Iterable<Map<String, Object>> execute(String cypherQuery, Map<String, ?> params) {
                return operations.query(cypherQuery, params);
            }
        });
    }


}
