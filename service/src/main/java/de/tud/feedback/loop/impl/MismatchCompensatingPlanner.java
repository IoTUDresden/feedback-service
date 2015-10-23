package de.tud.feedback.loop.impl;

import de.tud.feedback.CypherExecutor;
import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.domain.Command;
import de.tud.feedback.domain.ContextMismatch;
import de.tud.feedback.domain.Objective;
import de.tud.feedback.graph.SimpleCypherExecutor;
import de.tud.feedback.loop.ChangeRequest;
import de.tud.feedback.loop.MismatchProvider;
import de.tud.feedback.loop.Planner;
import de.tud.feedback.repository.CommandRepository;
import de.tud.feedback.repository.graph.ObjectiveRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;

import static java.lang.String.format;
import static org.joda.time.DateTime.now;

@Component
public class MismatchCompensatingPlanner implements Planner {

    private static final Logger LOG = LoggerFactory.getLogger(MismatchCompensatingPlanner.class);

    private ObjectiveRepository objectiveRepository;

    private CommandRepository commandRepository;

    private MismatchProvider mismatchProvider;

    private CypherExecutor executor;

    private FeedbackPlugin plugin;

    @PostConstruct
    public void init() {
        commandRepository = plugin.getCompensationRepository(executor);
        mismatchProvider = plugin.getMismatchProvider();
    }

    @Override
    public void plan(ChangeRequest changeRequest) {
        try {
            Objective objective = changeRequest.getObjective();
            Long measuringNodeId = changeRequest.getResult().getMeasuringNodeId();
            Collection<Command> commands = commandRepository.findCommandsManipulating(measuringNodeId);
            ContextMismatch mismatch = mismatchProvider.getMismatch(
                    objective.getSatisfiedExpression(),
                    changeRequest.getResult().getContextVariables());

            int a = commands.size();

            resetObjective(objective);

        } catch (RuntimeException exception) {
            failOn(changeRequest, exception.getMessage());
        }
    }

    private void resetObjective(Objective objective) {
        objective.setCreated(now());
        objective.setState(Objective.State.UNSATISFIED);
        objectiveRepository.save(objective);
    }

    private void failOn(ChangeRequest changeRequest, String cause) {
        Objective objective = changeRequest.getObjective();
        LOG.warn(format("Compensation of %s failed. %s", objective, cause));
        objective.setState(Objective.State.FAILED);
        objectiveRepository.save(objective);
    }

    @Autowired
    public void setObjectiveRepository(ObjectiveRepository objectiveRepository) {
        this.objectiveRepository = objectiveRepository;
    }

    @Autowired
    public void setFeedbackPlugin(FeedbackPlugin plugin) {
        this.plugin = plugin;
    }

    @Autowired
    public void setExecutor(SimpleCypherExecutor executor) {
        this.executor = executor;
    }

}
