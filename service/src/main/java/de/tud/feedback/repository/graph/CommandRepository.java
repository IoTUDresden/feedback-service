package de.tud.feedback.repository.graph;

import de.tud.feedback.domain.Command;
import de.tud.feedback.domain.Objective;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Set;

@RepositoryRestResource
public interface CommandRepository extends GraphRepository<Command> {

    @Query( "MATCH p = (c:Command)-[:executedFor]->(o:Objective) " +
            "WHERE ID(o) = {objectiveId} " +
            "RETURN p")
    Set<Command> findCommandsExecutedFor(@Param("objectiveId") Objective objective);

}
