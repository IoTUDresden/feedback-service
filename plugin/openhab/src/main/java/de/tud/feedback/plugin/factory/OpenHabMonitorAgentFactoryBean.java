package de.tud.feedback.plugin.factory;

import de.tud.feedback.plugin.OpenHabMonitorAgent;
import de.tud.feedback.plugin.openhab.OpenHabService;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import static java.lang.String.format;

public class OpenHabMonitorAgentFactoryBean extends AbstractFactoryBean<OpenHabMonitorAgent> {

    private String host;

    private Integer port;

    private Double numberStateChangeDelta = 0.01;

    private Integer pollingSeconds = 2;

    public static OpenHabMonitorAgentFactoryBean build() {
        return new OpenHabMonitorAgentFactoryBean();
    }

    public OpenHabMonitorAgentFactoryBean setPollingSeconds(Integer pollingSeconds) {
        this.pollingSeconds = pollingSeconds;
        return this;
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
        OpenHabMonitorAgent agent = new OpenHabMonitorAgent(
                Feign.builder()
                        .decoder(new JacksonDecoder())
                        .target(OpenHabService.class, format("http://%s:%s", host, port)));

        agent.setNumberStateChangeDelta(numberStateChangeDelta);
        agent.setPollingSeconds(pollingSeconds);

        return agent;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
