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
import java.util.List;

import static java.lang.String.format;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DelegatingExecutor implements Executor {

    private static final Logger LOG = LoggerFactory.getLogger(DelegatingExecutor.class);

    private final java.util.List<CommandExecutor> commandExecutors;

    @Autowired
    public DelegatingExecutor(List<CommandExecutor> commandExecutor) {
        this.commandExecutors = commandExecutor;
    }

    @Override
    public void execute(Command command) {
        try {
            commandExecutors.stream()
                    .filter(ex -> ex.supportsCommand(command))
                    .forEach(ex -> ex.execute(command));
        } catch (RuntimeException exception) {
            LOG.error(format("%s failed. %s", command, exception.getMessage()));
            command.setRepeatable(false);
        }
    }

}
