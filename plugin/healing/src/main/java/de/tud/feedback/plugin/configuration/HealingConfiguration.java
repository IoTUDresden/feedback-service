package de.tud.feedback.plugin.configuration;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@Configuration
@EnableNeo4jRepositories("de.tud.feedback.plugin.repository")
@ComponentScan("de.tud.feedback.plugin")
public class HealingConfiguration {
}
