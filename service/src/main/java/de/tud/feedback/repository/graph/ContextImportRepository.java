package de.tud.feedback.repository.graph;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.ContextImport;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface ContextImportRepository extends GraphRepository<ContextImport> {

    @Query( "MATCH (c:Context)<-[r:for]-(i:ContextImport) " +
            "WHERE id(c) = {contextId} " +
            "DELETE i, r")
    void deleteAllWithin(@Param("contextId") Context context);

}
