package de.tud.feedback.plugin;

import de.tud.feedback.api.ContextUpdater;
import de.tud.feedback.api.MonitorAgent;
import de.tud.feedback.api.annotation.LogInvocation;
import de.tud.feedback.plugin.openhab.ItemUpdateHandler;
import de.tud.feedback.plugin.openhab.OpenHabService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenHabMonitorAgent implements MonitorAgent {

    private static final Logger LOG = LoggerFactory.getLogger(OpenHabMonitorAgent.class);

    private final OpenHabService service;

    private Integer pollingSeconds = 2;

    private ItemUpdateHandler handler;

    public OpenHabMonitorAgent(OpenHabService service, ItemUpdateHandler handler) {
        this.service = service;
        this.handler = handler;
    }

    @Override
    @LogInvocation
    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        while (true) {
            processOpenHabItems();
            postponeNextRequest();
        }
    }

    @Override
    public void use(ContextUpdater updater) {
        handler.use(updater);
    }

    private void processOpenHabItems() {
        service.getAllItems().getItems().stream().forEach(handler::handle);
    }

    private void postponeNextRequest() {
        try {
            Thread.sleep(1000L * pollingSeconds);
        } catch (InterruptedException exception) {
            LOG.info(exception.getMessage());
        }
    }

    public void setPollingSeconds(Integer seconds) {
        this.pollingSeconds = seconds;
    }

}
