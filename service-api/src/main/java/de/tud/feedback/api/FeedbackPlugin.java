package de.tud.feedback.api;

import java.util.Collection;

public interface FeedbackPlugin {

    String name();

    ContextImporter getContextImporter(CypherExecutor executor);

    ContextUpdater getContextUpdater(CypherExecutor executor);

    Collection<MonitorAgent> getMonitorAgentsFor(Long contextId);

}
