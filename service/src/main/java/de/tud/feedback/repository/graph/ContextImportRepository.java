package de.tud.feedback.repository.graph;

import de.tud.feedback.domain.ContextImport;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "import", path = "import", exported = false)
public interface ContextImportRepository extends GraphRepository<ContextImport> {
}
