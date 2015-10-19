package de.tud.feedback.repository.graph;

import de.tud.feedback.domain.Goal;
import de.tud.feedback.domain.Workflow;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Set;

@RepositoryRestResource
public interface GoalRepository extends GraphRepository<Goal> {

    @Query( "MATCH p = (o:Objective)<-[:hasObjective]-(g:Goal)<-[:hasGoal]-(w:Workflow) " +
            "WHERE ID(w) = {workflowId} " +
            "RETURN p")
    Set<Goal> findGoalsFor(@Param("workflowId") Workflow workflow);

}
