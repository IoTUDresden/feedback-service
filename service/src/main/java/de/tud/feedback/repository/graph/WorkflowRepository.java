package de.tud.feedback.repository.graph;

import de.tud.feedback.domain.Workflow;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "workflow", path = "workflow")
public interface WorkflowRepository extends GraphRepository<Workflow> {
}
