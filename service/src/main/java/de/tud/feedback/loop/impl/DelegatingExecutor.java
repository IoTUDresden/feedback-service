package de.tud.feedback.loop.impl;

import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.domain.Command;
import de.tud.feedback.loop.CommandExecutor;
import de.tud.feedback.loop.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DelegatingExecutor implements Executor {

    private static final Logger LOG = LoggerFactory.getLogger(DelegatingExecutor.class);

    private CommandExecutor commandExecutor;

    @Override
    public void execute(Command command) {
        try {
            commandExecutor.execute(command);
        } catch (RuntimeException exception) {
            LOG.error("Command execution failed. " + exception.getMessage());
        }
    }

    @Autowired
    public void setPlugin(FeedbackPlugin plugin) {
        this.commandExecutor = plugin.getExecutor();
    }

}
