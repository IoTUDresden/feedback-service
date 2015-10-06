package de.tud.feedback.plugin.factory;

import de.tud.feedback.plugin.OpenHabMonitorAgent;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class OpenHabMonitorAgentFactoryBean extends AbstractFactoryBean<OpenHabMonitorAgent> {

    private String host;

    private Integer port;

    private Double numberStateChangeDelta;

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

    public OpenHabMonitorAgentFactoryBean setNumberStateChangeDelta(Double numberStateChangeDelta) {
        this.numberStateChangeDelta = numberStateChangeDelta;
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return OpenHabMonitorAgent.class;
    }

    @Override
    protected OpenHabMonitorAgent createInstance() throws Exception {
        OpenHabMonitorAgent agent = new OpenHabMonitorAgent(host, port);
        agent.setNumberStateChangeDelta(numberStateChangeDelta);
        return agent;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
