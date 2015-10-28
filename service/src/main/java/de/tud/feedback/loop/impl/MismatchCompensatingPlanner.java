package de.tud.feedback.loop.impl;

import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.domain.ChangeRequest;
import de.tud.feedback.domain.Command;
import de.tud.feedback.domain.ContextMismatch;
import de.tud.feedback.domain.Objective;
import de.tud.feedback.graph.SimpleCypherExecutor;
import de.tud.feedback.loop.MismatchProvider;
import de.tud.feedback.loop.Planner;
import de.tud.feedback.repository.CompensationRepository;
import de.tud.feedback.repository.graph.CommandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

import static java.lang.String.format;
import static org.joda.time.DateTime.now;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MismatchCompensatingPlanner implements Planner {

    private static final Logger LOG = LoggerFactory.getLogger(MismatchCompensatingPlanner.class);

    private final CommandRepository commandRepository;

    private final CompensationRepository compensationRepository;

    private final MismatchProvider mismatchProvider;

    @Autowired
    public MismatchCompensatingPlanner(
            FeedbackPlugin plugin, CommandRepository commandRepository, SimpleCypherExecutor executor) {
        this.commandRepository = commandRepository;
        mismatchProvider = plugin.getMismatchProvider();
        compensationRepository = plugin.getCompensationRepository(executor);
    }

    @Override
    public Optional<Command> plan(ChangeRequest changeRequest) {
        Objective objective = changeRequest.getObjective();
        ContextMismatch mismatch = mismatchWithin(changeRequest);
        Long measuringNodeId = changeRequest.getResult().getMeasuringNodeId();
        Collection<Command> executedCommands = commandRepository.findCommandsExecutedFor(objective);
        Collection<Command> manipulatingCommands = compensationRepository.findCommandsManipulating(measuringNodeId);

        try {
            Optional<Command> compensation = manipulatingCommands.stream()
                    .filter(command -> command.isRepeatable() || !executedCommands.contains(command))
                    .filter(command -> isCompensating(mismatch, command))
                    .findAny();

            if (compensation.isPresent()) {
                prepare(compensation.get(), objective);
                return compensation;

            } else {
                objective.setState(Objective.State.FAILED);
            }

        } catch (RuntimeException exception) {
            LOG.error(format("%s failed due to %s", objective, exception.getMessage()));
            objective.setState(Objective.State.FAILED);
        }

        return Optional.empty();
    }

    private void prepare(Command compensation, Objective objective) {
        compensation.setObjective(objective);
        objective.getCommands().add(compensation);
        objective.setCreated(now());
        objective.setState(Objective.State.UNSATISFIED);
    }

    private boolean isCompensating(ContextMismatch mismatch, Command command) {
        switch (mismatch.getType()) {
            case TOO_LOW:  return command.getType() == Command.Type.UP;
            case TOO_HIGH: return command.getType() == Command.Type.DOWN;
            case UNEQUAL:  return command.getType() == Command.Type.ASSIGN;
            default: return false;
        }
    }

    private ContextMismatch mismatchWithin(ChangeRequest changeRequest) {
        return mismatchProvider.getMismatch(
                changeRequest.getObjective().getSatisfiedExpression(),
                changeRequest.getResult().getContextVariables());
    }

}
