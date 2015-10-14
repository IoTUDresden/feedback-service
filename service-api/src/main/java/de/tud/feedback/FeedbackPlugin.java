package de.tud.feedback;

import java.util.Collection;

public interface FeedbackPlugin {

    String name();

    ContextImporter contextImporter(CypherExecutor executor);

    ContextUpdater contextUpdater(CypherExecutor executor);

    Collection<MonitorAgent> monitorAgents();

    WorkflowAugmentation workflowAugmentation();

}
