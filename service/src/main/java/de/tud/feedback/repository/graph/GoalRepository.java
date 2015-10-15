package de.tud.feedback.repository.graph;

import de.tud.feedback.domain.Goal;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface GoalRepository extends GraphRepository<Goal> {
}
