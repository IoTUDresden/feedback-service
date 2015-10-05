package de.tud.feedback.repository;

import de.tud.feedback.domain.Context;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Set;

@RepositoryRestResource(collectionResourceRel = "context", path = "context")
public interface ContextRepository extends GraphRepository<Context> {

    @Query("START n = NODE(*) WHERE NOT n-[*..2]->() RETURN ID(n) AS ID")
    Set<Integer> findOrphanedNodeIds();

}
