package de.tud.feedback.loop.impl;

import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.domain.Command;
import de.tud.feedback.domain.Objective;
import de.tud.feedback.event.ObjectiveFailedEvent;
import de.tud.feedback.loop.CommandExecutor;
import de.tud.feedback.loop.Executor;
import de.tud.feedback.repository.graph.ObjectiveRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class DelegatingExecutor implements Executor {

    private static final Logger LOG = LoggerFactory.getLogger(DelegatingExecutor.class);

    private CommandExecutor commandExecutor;

    private ObjectiveRepository objectiveRepository;

    private ApplicationEventPublisher publisher;

    @Override
    public void execute(Command command) {
        try {
            commandExecutor.execute(command);
        } catch (RuntimeException exception) {
            LOG.error(format("%s failed. %s", command, exception.getMessage()));
            failOn(command.getObjective());
        }
    }

    private void failOn(Objective objective) {
        objective.setState(Objective.State.FAILED);
        objectiveRepository.save(objective);
        publisher.publishEvent(ObjectiveFailedEvent.on(objective));
    }

    @Autowired
    public void setPlugin(FeedbackPlugin plugin) {
        this.commandExecutor = plugin.getExecutor();
    }

    @Autowired
    public void setObjectiveRepository(ObjectiveRepository objectiveRepository) {
        this.objectiveRepository = objectiveRepository;
    }

    @Autowired
    public void setPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

}
