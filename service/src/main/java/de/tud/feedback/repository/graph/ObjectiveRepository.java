package de.tud.feedback.repository.graph;

import de.tud.feedback.domain.Goal;
import de.tud.feedback.domain.Objective;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface ObjectiveRepository extends GraphRepository<Objective> {

    @Query( "MATCH (g:Goal)<-[r:hasObjective]-(o:Objective) " +
            "WHERE id(g) = {goalId} " +
            "DELETE o, r")
    void deleteAllFor(@Param("goalId") Goal goal);

}
