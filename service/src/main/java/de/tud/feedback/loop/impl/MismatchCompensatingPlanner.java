package de.tud.feedback.loop.impl;

import de.tud.feedback.CypherExecutor;
import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.domain.Command;
import de.tud.feedback.domain.ContextMismatch;
import de.tud.feedback.domain.Objective;
import de.tud.feedback.event.ExecuteRequestedEvent;
import de.tud.feedback.event.ObjectiveFailedEvent;
import de.tud.feedback.graph.SimpleCypherExecutor;
import de.tud.feedback.loop.ChangeRequest;
import de.tud.feedback.loop.MismatchProvider;
import de.tud.feedback.loop.Planner;
import de.tud.feedback.repository.CompensationRepository;
import de.tud.feedback.repository.graph.CommandRepository;
import de.tud.feedback.repository.graph.ObjectiveRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Optional;

import static java.lang.String.format;
import static org.joda.time.DateTime.now;

@Component
public class MismatchCompensatingPlanner implements Planner {

    private static final Logger LOG = LoggerFactory.getLogger(MismatchCompensatingPlanner.class);

    private ApplicationEventPublisher publisher;

    private ObjectiveRepository objectiveRepository;

    private CompensationRepository compensationRepository;

    private CommandRepository commandRepository;

    private MismatchProvider mismatchProvider;

    private CypherExecutor executor;

    private FeedbackPlugin plugin;

    @PostConstruct
    public void init() {
        compensationRepository = plugin.getCompensationRepository(executor);
        mismatchProvider = plugin.getMismatchProvider();
    }

    @Override
    public void plan(ChangeRequest changeRequest) {
        Objective objective = changeRequest.getObjective();
        ContextMismatch mismatch = mismatchWithin(changeRequest);
        Long measuringNodeId = changeRequest.getResult().getMeasuringNodeId();
        Collection<Command> executedCommands = commandRepository.findCommandsExecutedFor(objective);
        Collection<Command> manipulatingCommands = compensationRepository.findCommandsManipulating(measuringNodeId);

        try {
            Optional<Command> compensation = manipulatingCommands.stream()
                    .filter(command -> !executedCommands.contains(command))
                    .filter(command -> isCompensating(mismatch, command))
                    .findAny();

            if (compensation.isPresent()) {
                compensate(objective, compensation.get());
            } else {
                failOn(changeRequest);
            }

        } catch (RuntimeException exception) {
            LOG.error(format("%s failed due to %s", objective, exception.getMessage()));
            failOn(changeRequest);
        }
    }

    private void compensate(Objective objective, Command compensation) {
        compensation.setObjective(objective);
        commandRepository.save(compensation);

        objective.setCreated(now());
        objective.getCommands().add(compensation);
        objective.setState(Objective.State.UNSATISFIED);
        objectiveRepository.save(objective);

        publisher.publishEvent(ExecuteRequestedEvent.on(compensation));
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

    private void failOn(ChangeRequest changeRequest) {
        Objective objective = changeRequest.getObjective();
        objective.setState(Objective.State.FAILED);
        objectiveRepository.save(objective);
        publisher.publishEvent(ObjectiveFailedEvent.on(objective));
    }

    @Autowired
    public void setObjectiveRepository(ObjectiveRepository objectiveRepository) {
        this.objectiveRepository = objectiveRepository;
    }

    @Autowired
    public void setCommandRepository(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }

    @Autowired
    public void setFeedbackPlugin(FeedbackPlugin plugin) {
        this.plugin = plugin;
    }

    @Autowired
    public void setExecutor(SimpleCypherExecutor executor) {
        this.executor = executor;
    }

    @Autowired
    public void setPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

}
