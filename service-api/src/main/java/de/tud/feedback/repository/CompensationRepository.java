package de.tud.feedback.repository;

import de.tud.feedback.domain.Command;

import java.util.Set;

public interface CompensationRepository {

    Set<Command> findCommandsManipulating(Long testNodeId);

}
