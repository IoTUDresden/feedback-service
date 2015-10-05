package de.tud.feedback.service.impl;

import com.google.common.collect.ImmutableMap;
import de.tud.feedback.annotation.LogInvocation;
import de.tud.feedback.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class Neo4jKnowledgeService implements KnowledgeService {

    private Neo4jOperations operations;

    @LogInvocation
    @PostConstruct
    public void initialize() {
        operations.query("CREATE CONSTRAINT ON (c:Workflow) ASSERT c.name IS UNIQUE", params().build());
        operations.query("CREATE CONSTRAINT ON (c:Context)  ASSERT c.name IS UNIQUE", params().build());
    }

    private ImmutableMap.Builder<String, Object> params() {
        return new ImmutableMap.Builder<>();
    }

    @Autowired
    public void setNeo4jOperations(Neo4jOperations operations) {
        this.operations = operations;
    }

}
