package de.tud.feedback.repository.graph;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Workflow;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Set;

@RepositoryRestResource
public interface WorkflowRepository extends GraphRepository<Workflow> {

    @Query( "MATCH p = (o:Objective)<-[:hasObjective]-(g:Goal)<-[:hasGoal]-(w:Workflow)-[:runsWithin]->(c:Context) " +
            "WHERE ID(c) = {contextId} " +
            "RETURN p")
    Set<Workflow> findWorkflowsWithin(@Param("contextId") Context context);

}
