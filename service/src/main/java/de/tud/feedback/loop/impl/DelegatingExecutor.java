package de.tud.feedback.loop.impl;

import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.annotation.GraphTransactional;
import de.tud.feedback.domain.Command;
import de.tud.feedback.loop.CommandExecutor;
import de.tud.feedback.loop.Executor;
import de.tud.feedback.repository.graph.CommandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class DelegatingExecutor implements Executor {

    private static final Logger LOG = LoggerFactory.getLogger(DelegatingExecutor.class);

    private CommandExecutor commandExecutor;

    private CommandRepository commandRepository;

    @Override
    @GraphTransactional
    public void execute(Command command) {
        try {
            commandExecutor.execute(command);
        } catch (RuntimeException exception) {
            LOG.error(format("%s failed. %s", command, exception.getMessage()));
            command.setRepeatable(false);
            commandRepository.save(command);
        }
    }

    @Autowired
    public void setPlugin(FeedbackPlugin plugin) {
        this.commandExecutor = plugin.getExecutor();
    }

    @Autowired
    public void setCommandRepository(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }

}
