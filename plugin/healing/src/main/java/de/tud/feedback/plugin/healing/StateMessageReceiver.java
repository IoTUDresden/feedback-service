package de.tud.feedback.plugin.healing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.jms.annotation.JmsListener;

/**
 * Created by Stefan on 09.05.2016.
 */
@Component
public class StateMessageReceiver {

    @Autowired
    ConfigurableApplicationContext context;

    @JmsListener(destination = "messages")
    public void receiveMessage(String message) {

        System.out.println("Received <" + message + ">");

    }
}
