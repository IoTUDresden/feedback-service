package de.tud.feedback.plugin;


import com.google.common.base.Optional;
import de.tud.feedback.CypherExecutor;
import de.tud.feedback.Utils;
import de.tud.feedback.domain.Command;
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
        if (!Optional.fromNullable(testNodeId).isPresent()) {
            LOG.warn("Cannot find commands without a valid testNodeId from the context path.");
            return newHashSet();
        }
        //TODO We need the instance id of the process at this point
        // so testNode id should point at this
        return executor.execute(query, params()
                .put("stateId", testNodeId)
                .build())
                .stream()
                .map(this::toCommand)
                .collect(toSet());
    }

    private Command toCommand(Map<String, Object> attributes){
        return new ProteusCommand()
                .setIp("ip")
                .setPeeId("peerId")
                .setRepeatable(false);

        //TODO is the following important?
//                .setTargetTo((String) attributes.get("actuator"))
//                .setNameTo((String) attributes.get("commandName"))
//                .setTypeTo(commands.get(attributes.get("commandType")));
    }

    public static class ProteusCommand extends Command{
        private String peerId;
        private String ip;

        private ProteusCommand setPeeId(String peeId){
            this.peerId = peeId;
            return this;
        }
        private ProteusCommand setIp(String ip){
            this.ip = ip;
            return this;
        }

        public String getPeerId() {
            return peerId;
        }

        public String getIp() {
            return ip;
        }
    }
}
