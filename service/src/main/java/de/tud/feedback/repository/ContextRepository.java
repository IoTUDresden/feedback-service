package de.tud.feedback.repository;

import de.tud.feedback.domain.context.Context;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "context", path = "context")
public interface ContextRepository extends CrudRepository<Context, Long> {
}
