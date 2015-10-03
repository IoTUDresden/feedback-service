package de.tud.feedback.plugin.factory;

import de.tud.feedback.plugin.OpenHabMonitorAgent;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class OpenHabMonitorAgentFactoryBean extends AbstractFactoryBean<OpenHabMonitorAgent> {

    private String host;

    private Integer port;

    public static OpenHabMonitorAgentFactoryBean build() {
        return new OpenHabMonitorAgentFactoryBean();
    }

    public OpenHabMonitorAgentFactoryBean setHost(String host) {
        this.host = host;
        return this;
    }

    public OpenHabMonitorAgentFactoryBean setPort(Integer port) {
        this.port = port;
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return OpenHabMonitorAgent.class;
    }

    @Override
    protected OpenHabMonitorAgent createInstance() throws Exception {
        return new OpenHabMonitorAgent(host, port);
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
