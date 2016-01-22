package de.tud.feedback.plugin;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.annotation.LogInvocation;
import de.tud.feedback.loop.MonitorAgent;
import de.tud.feedback.plugin.openhab.RelevancyFilter;
import de.tud.feedback.plugin.openhab.OpenHabItem;
import de.tud.feedback.plugin.openhab.OpenHabService;

public class OpenHabMonitorAgent implements MonitorAgent {

    private final OpenHabService service;

    private final RelevancyFilter filter;

    private ContextUpdater updater;

    private Integer pollingSeconds = 2;

    public OpenHabMonitorAgent(OpenHabService service, RelevancyFilter filter) {
        this.service = service;
        this.filter = filter;
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
                .filter(filter::isRelevant)
                .forEach(it -> updater.update(it.getName(), it.getState()));
    }

    public void setPollingSeconds(Integer seconds) {
        this.pollingSeconds = seconds;
    }

    @Override
    public void workWith(ContextUpdater updater) {
        this.updater = updater;
    }

}
