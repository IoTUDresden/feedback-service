package de.tud.feedback.plugin.factory;

import de.tud.feedback.plugin.OpenHabMonitorAgent;
import de.tud.feedback.plugin.openhab.ItemUpdateHandler;
import de.tud.feedback.plugin.openhab.OpenHabService;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class OpenHabMonitorAgentFactoryBean extends AbstractFactoryBean<OpenHabMonitorAgent> {

    private Double numberStateChangeDelta = 0.01;

    private Integer pollingSeconds = 1;

    private OpenHabService service;

    @Override
    protected OpenHabMonitorAgent createInstance() throws Exception {
        checkNotNull(service, "OpenHabService is missing");

        final ItemUpdateHandler handler = new ItemUpdateHandler(numberStateChangeDelta);
        final OpenHabMonitorAgent agent = new OpenHabMonitorAgent(service, handler);

        agent.setPollingSeconds(pollingSeconds);

        return agent;
    }

    public OpenHabMonitorAgentFactoryBean setNumberStateChangeDelta(Double numberStateChangeDelta) {
        this.numberStateChangeDelta = checkNotNull(numberStateChangeDelta);
        return this;
    }

    public OpenHabMonitorAgentFactoryBean setPollingSeconds(Integer pollingSeconds) {
        this.pollingSeconds = checkNotNull(pollingSeconds);
        return this;
    }

    public OpenHabMonitorAgentFactoryBean setService(OpenHabService service) {
        this.service = checkNotNull(service);
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
