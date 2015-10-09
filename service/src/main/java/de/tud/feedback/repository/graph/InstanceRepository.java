package de.tud.feedback.repository.graph;

import de.tud.feedback.domain.WorkflowInstance;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "instance", path = "instance")
public interface InstanceRepository extends GraphRepository<WorkflowInstance> {
}
