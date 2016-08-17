package de.tud.feedback.plugin;

import com.google.common.collect.ImmutableMap;
import de.tud.feedback.CypherExecutor;
import de.tud.feedback.domain.Command;
import de.tud.feedback.repository.CompensationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

import static de.tud.feedback.Utils.params;
import static java.util.stream.Collectors.toSet;

/**
 * Created by Stefan on 14.06.2016.
 */
public class HealingCompensationRepository implements CompensationRepository {
    private static final Logger LOG = LoggerFactory.getLogger(HealingCompensationRepository.class);

    private static final String TAKE   = "StartCommand";

    private static final Map<Object, Command.Type> commands = ImmutableMap.<Object, Command.Type>builder()
            .put(TAKE, Command.Type.TAKE)
            .build();

    private final CypherExecutor executor;

    private final String query;

    public HealingCompensationRepository(CypherExecutor executor, String query) {
        this.executor = executor;
        this.query = query;
    }

    @Override
    public Set<Command> findCommandsManipulating(Long testNodeId) {
        Set<Command> commands = executor.execute(query, params()
                .put("stateId", testNodeId)
                .build())
                .stream()
                .map(this::toCommand)
                .collect(toSet());
        LOG.debug("Compensation Repository: Found " + commands.size() + " commands");
        return commands;
    }

    private Command toCommand(Map<String, Object> attributes) {
        return new Command()
                .setRepeatable(true) //TODO: Versuch es nochmal wenn es nciht geklappt hat?
                .setTargetTo((String) attributes.get("newPeer"))
                .setProcessName((String) attributes.get("processName"))
                .setNameTo((String) attributes.get("commandName"))
                .setCommandReceiverId((String) attributes.get("oldPeer"))
                .setCommandAddress((String) attributes.get("ipAddress"))
                .setTypeTo(commands.get(attributes.get("commandType")));
    }
}
