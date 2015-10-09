package de.tud.feedback.plugin.factory;

import de.tud.feedback.plugin.OpenHabMonitorAgent;
import de.tud.feedback.plugin.openhab.ItemUpdateHandler;
import de.tud.feedback.plugin.openhab.OpenHabService;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

@Component
public class OpenHabMonitorAgentFactoryBean extends AbstractFactoryBean<OpenHabMonitorAgent> {

    private String host;

    private Integer port;

    private Double numberStateChangeDelta = 0.01;

    private Integer pollingSeconds = 1;

    @Override
    protected OpenHabMonitorAgent createInstance() throws Exception {
        checkNotNull(host, "Host is missing");
        checkNotNull(port, "Port is missing");

        final ItemUpdateHandler handler = new ItemUpdateHandler(numberStateChangeDelta);
        final OpenHabMonitorAgent agent = new OpenHabMonitorAgent(createService(), handler);

        agent.setPollingSeconds(pollingSeconds);

        return agent;
    }

    private OpenHabService createService() {
        return Feign.builder()
                .decoder(new JacksonDecoder())
                .target(OpenHabService.class, format("http://%s:%s", host, port));
    }

    public OpenHabMonitorAgentFactoryBean setHost(String host) {
        this.host = checkNotNull(host);
        return this;
    }

    public OpenHabMonitorAgentFactoryBean setPort(Integer port) {
        this.port = checkNotNull(port);
        return this;
    }

    public OpenHabMonitorAgentFactoryBean setNumberStateChangeDelta(Double numberStateChangeDelta) {
        this.numberStateChangeDelta = checkNotNull(numberStateChangeDelta);
        return this;
    }

    public OpenHabMonitorAgentFactoryBean setPollingSeconds(Integer pollingSeconds) {
        this.pollingSeconds = checkNotNull(pollingSeconds);
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return OpenHabMonitorAgent.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
