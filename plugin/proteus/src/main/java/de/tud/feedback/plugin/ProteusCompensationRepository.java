package de.tud.feedback.plugin;


import de.tud.feedback.CypherExecutor;
import de.tud.feedback.domain.Command;
import de.tud.feedback.plugin.domain.ProteusCommand;
import de.tud.feedback.repository.CompensationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static de.tud.feedback.Utils.params;
import static java.util.stream.Collectors.toSet;

public class ProteusCompensationRepository implements CompensationRepository {
    private static final Logger LOG = LoggerFactory.getLogger(DogOntCompensationRepository.class);

    private final CypherExecutor executor;
    private final String query;

    public ProteusCompensationRepository(CypherExecutor executor, String query) {
        this.executor = executor;
        this.query = query;
    }

    @Override
    public Set<Command> findCommandsManipulating(Long testNodeId) {
        //TODO We need the instance id (or session id for this process)
        return executor.execute(query, params()
                .build())
                .stream()
                .map(this::toCommand)
                .collect(toSet());
    }

    private Command toCommand(Map<String, Object> attributes){
        String ip = (String)attributes.get("ip");
        String peerId = (String)attributes.get("peerId");

        return new ProteusCommand()
                .setIp(ip)
                .setPeeId(peerId)
                .setTargetTo(peerId + "_" + ip)
                .setNameTo("ExecuteOnOtherPeerCommand")
                .setTypeTo(Command.Type.ASSIGN)
                .setRepeatable(false);
    }
}
