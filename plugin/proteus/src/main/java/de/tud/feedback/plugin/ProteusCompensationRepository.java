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
        return null;
    }

    private Command toCommand(Map<String, Object> attributes){
        return null;
    }
}
