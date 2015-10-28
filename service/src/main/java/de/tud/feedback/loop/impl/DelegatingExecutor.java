package de.tud.feedback.loop.impl;

import de.tud.feedback.domain.Command;
import de.tud.feedback.loop.CommandExecutor;
import de.tud.feedback.loop.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DelegatingExecutor implements Executor {

    private static final Logger LOG = LoggerFactory.getLogger(DelegatingExecutor.class);

    private final CommandExecutor commandExecutor;

    @Autowired
    public DelegatingExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    @Override
    public void execute(Command command) {
        try {
            commandExecutor.execute(command);
        } catch (RuntimeException exception) {
            LOG.error(format("%s failed. %s", command, exception.getMessage()));
            command.setRepeatable(false);
        }
    }

}
