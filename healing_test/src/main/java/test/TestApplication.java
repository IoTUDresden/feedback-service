package test;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import test.jms.SimpleProducer;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

/**
 * Created by Stefan on 17.05.2016.
 */
@SpringBootApplication
@EnableJms
public class TestApplication {

    @Autowired
    private SimpleProducer producer;

    @Bean
    public Queue queue() {
        return new ActiveMQQueue("client.messages");
    }
    @Bean
    ConnectionFactory connectionFactory(){
        return new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }


}
