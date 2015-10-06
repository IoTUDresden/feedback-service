package de.tud.feedback.api;

import java.util.Collection;

public interface FeedbackPlugin {

    String name();

    ContextImporter contextImporter(CypherExecutor executor);

    ContextUpdater contextUpdater(CypherExecutor executor);

    Collection<MonitorAgent> monitorAgents();

}
