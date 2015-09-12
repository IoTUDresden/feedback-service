package de.tud.feedback.repository;

import de.tud.feedback.domain.Workflow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface WorkflowRepository extends CrudRepository<Workflow, Long> {
}
