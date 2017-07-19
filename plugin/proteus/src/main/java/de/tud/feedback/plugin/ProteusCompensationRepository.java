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
    private final CypherExecutor executor;
    private final String query;

    public ProteusCompensationRepository(CypherExecutor executor, String query) {
        this.executor = executor;
        this.query = query;
    }

    @Override
    public Set<Command> findCommandsManipulating(Long testNodeId) {
        return executor.execute(query, params()
                .put("processNodeId", testNodeId)
                .build())
                .stream()
                .map(this::toCommand)
                .collect(toSet());
    }

    private Command toCommand(Map<String, Object> attributes){
        Map<String, Object> process = (Map<String, Object>)attributes.get("process");
        Map<String, Object> peer = (Map<String, Object>)attributes.get("peer");
        Map<String, Object> executingPeer = (Map<String, Object>)attributes.get("executingPeer");

        String processId = (String)process.get("processId");
        String processName = (String)process.get("name");
        String processModelId = (String)process.get("processModelId");
        String ip = (String)peer.get("ip");
        String peerId = (String)peer.get("peerId");
        String oldPeerId = (String)executingPeer.get("peerId");

        return new ProteusCommand()
                .setOldInstanceId(processId)
                .setOldPeerId(oldPeerId)
                .setProcessModelId(processModelId)
                .setNewIp(ip)
                .setNewPeerId(peerId)
                .setTargetTo(peerId + "_" + ip)
                .setNameTo("Compensate_" + processName + "_" + processId + "_OnNew_" + peerId)
                .setTypeTo(Command.Type.ASSIGN)
                .setRepeatable(false);
    }
}
