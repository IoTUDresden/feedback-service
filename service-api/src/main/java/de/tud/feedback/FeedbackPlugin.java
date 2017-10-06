package de.tud.feedback;

import de.tud.feedback.loop.CommandExecutor;
import de.tud.feedback.loop.MismatchProvider;
import de.tud.feedback.loop.MonitorAgent;
import de.tud.feedback.loop.ObjectiveEvaluator;
import de.tud.feedback.repository.CompensationRepository;

import java.util.Collection;
import java.util.List;

public interface FeedbackPlugin {

    ContextImporter getContextImporter(CypherExecutor executor);

    ContextUpdater getContextUpdater(CypherExecutor executor);

    ObjectiveEvaluator getObjectiveEvaluator(CypherExecutor executor);

    List<CompensationRepository> getCompensationRepositories(CypherExecutor executor);

    MismatchProvider getMismatchProvider();

    Collection<MonitorAgent> getMonitorAgents();

    CommandExecutor getExecutor();

}
