package de.tud.feedback;

import java.util.Collection;

public interface FeedbackPlugin {

    ContextImporter contextImporter(CypherExecutor executor);

    ContextUpdater contextUpdater(CypherExecutor executor);

    Collection<MonitorAgent> monitorAgents();

}
