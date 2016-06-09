package de.tud.feedback.plugin.test;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.jms.ConnectionFactory;

/**
 * Created by Stefan on 17.05.2016.
 */
@SpringBootApplication
public class TestApplication {
    @Bean
    ConnectionFactory connectionFactory(){
        return new ActiveMQConnectionFactory("tcp://localhost:61616");
    }
}
