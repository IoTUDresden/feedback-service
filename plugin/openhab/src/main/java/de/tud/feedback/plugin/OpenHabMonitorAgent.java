package de.tud.feedback.plugin;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.annotation.LogInvocation;
import de.tud.feedback.loop.MonitorAgent;
import de.tud.feedback.plugin.openhab.ItemUpdateHandler;
import de.tud.feedback.plugin.openhab.OpenHabItem;
import de.tud.feedback.plugin.openhab.OpenHabService;

public class OpenHabMonitorAgent implements MonitorAgent {

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
            try {
                processOpenHabItems();
                Thread.sleep(1000L * pollingSeconds);

            } catch (InterruptedException exception) {
                break;
            }
        }
    }

    private void processOpenHabItems() {
        service.getAllItems().stream()
                .filter(OpenHabItem::hasValidState)
                .filter(OpenHabItem::isUsefulItem)
                .forEach(handler::handle);
    }

    public void setPollingSeconds(Integer seconds) {
        this.pollingSeconds = seconds;
    }

    @Override
    public void workWith(ContextUpdater updater) {
        handler.setUpdater(updater);
    }

}
