package de.tud.feedback.service;

import java.util.Set;

public interface KnowledgeService {

    Set<Long> findOrphanedNodes();

}
