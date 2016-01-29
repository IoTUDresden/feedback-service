package de.tud.feedback.plugin;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.annotation.LogInvocation;
import de.tud.feedback.loop.MonitorAgent;
import de.tud.feedback.plugin.openhab.OpenHabItem;
import de.tud.feedback.plugin.openhab.OpenHabService;
import de.tud.feedback.plugin.openhab.SignificanceFilter;
import feign.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenHabMonitorAgent implements MonitorAgent {

    private static final Logger LOG = LoggerFactory.getLogger(OpenHabMonitorAgent.class);

    private final OpenHabService service;

    private final SignificanceFilter filter;

    private ContextUpdater updater;

    private Integer pollingSeconds = 2;

    public OpenHabMonitorAgent(OpenHabService service, SignificanceFilter filter) {
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

            } catch (RetryableException exception) {
                LOG.error("OpenHAB refused connection");
                break;

            } catch (InterruptedException exception) {
                LOG.info("Shutting down");
                break;
            }
        }
    }

    private void processOpenHabItems() {
        service.getAllItems().stream()
                .filter(OpenHabItem::hasValidState)
                .filter(OpenHabItem::isUsefulItem)
                .filter(filter::containsSignificantChange)
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
