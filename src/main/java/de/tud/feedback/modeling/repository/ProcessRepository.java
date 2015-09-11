package de.tud.feedback.modeling.repository;

import de.tud.feedback.domain.Process;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "process", path = "process")
public interface ProcessRepository extends PagingAndSortingRepository<Process, Long> {
}
