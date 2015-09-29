package de.tud.feedback.repository;

import de.tud.feedback.domain.context.ContextImport;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "import", path = "import", exported = false)
public interface ContextImportRepository extends CrudRepository<ContextImport, Long> {



}
