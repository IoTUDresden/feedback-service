package de.tud.feedback.repository;

import de.tud.feedback.domain.Context;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "context", path = "context")
public interface ContextRepository extends GraphRepository<Context> {
}
