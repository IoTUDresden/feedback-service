package de.tud.feedback.repository;

import de.tud.feedback.domain.process.Instance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface InstanceRepository extends CrudRepository<Instance, Long> {
}
