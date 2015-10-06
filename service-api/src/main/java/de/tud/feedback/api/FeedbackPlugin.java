package de.tud.feedback.api;

import java.util.Collection;

public interface FeedbackPlugin {

    String name();

    ContextImporter contextImporter(CypherExecutor executor);

    ContextUpdater contextUpdaterFor(ContextReference context, CypherExecutor executor);

    Collection<MonitorAgent> monitorAgents();

}
