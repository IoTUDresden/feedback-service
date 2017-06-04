package de.tud.feedback.plugin;

import de.tud.feedback.annotation.LogInvocation;
import de.tud.feedback.domain.Command;
import de.tud.feedback.loop.CommandExecutor;
import de.tud.feedback.plugin.openhab.OpenHabService;

import java.util.function.Function;

public class OpenHabCommandExecutor implements CommandExecutor {

    private final OpenHabService service;

    private final Function<String, String> itemNameMapper;

    public OpenHabCommandExecutor(OpenHabService service, Function<String, String> itemNameMapper) {
        this.itemNameMapper = itemNameMapper;
        this.service = service;
    }

    @Override
    @LogInvocation
    public void execute(Command command) {
        service.executeCommand(
                itemNameMapper.apply(command.getTarget()),
                command.getName());
    }

    @Override
    public boolean supportsCommand(Command command) {
        return Command.class.equals(command.getClass());
    }

}
