package de.tud.feedback.plugin;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.MonitorAgent;
import de.tud.feedback.annotation.LogInvocation;
import de.tud.feedback.plugin.openhab.ItemUpdateHandler;
import de.tud.feedback.plugin.openhab.OpenHabService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenHabMonitorAgent implements MonitorAgent {

    private static final Logger LOG = LoggerFactory.getLogger(OpenHabMonitorAgent.class);

    private final OpenHabService service;

    private final ItemUpdateHandler handler;

    private Integer pollingSeconds = 2;

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

    private void processOpenHabItems() {
        service.getAllItems().stream().forEach(handler::handle);
    }

    private void postponeNextRequest() {
        try {
            Thread.sleep(1000L * pollingSeconds);
        } catch (InterruptedException exception) {
            LOG.debug(exception.getMessage());
        }
    }

    public void setPollingSeconds(Integer seconds) {
        this.pollingSeconds = seconds;
    }

    @Override
    public void workWith(ContextUpdater updater) {
        handler.setUpdater(updater);
    }

}
