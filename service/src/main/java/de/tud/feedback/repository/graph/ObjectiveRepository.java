package de.tud.feedback.repository.graph;

import de.tud.feedback.domain.Objective;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface ObjectiveRepository extends GraphRepository<Objective> {
}
