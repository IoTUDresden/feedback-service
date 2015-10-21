package de.tud.feedback;

import java.util.Collection;

public interface FeedbackPlugin {

    ContextImporter getContextImporter(CypherExecutor executor);

    ContextUpdater getContextUpdater(CypherExecutor executor);

    Collection<MonitorAgent> getMonitorAgents();

}
