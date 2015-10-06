package de.tud.feedback.service.impl;

import com.google.common.collect.ImmutableMap;
import de.tud.feedback.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;

@Service
public class Neo4jKnowledgeService implements KnowledgeService {

    private Neo4jOperations operations;

    @PostConstruct
    public void initialize() {
        uniqueNameConstraintOn("Workflow", "Context", "ContextImport");
    }

    private void uniqueNameConstraintOn(String... labels) {
        newArrayList(labels).forEach(label -> q(format("CREATE CONSTRAINT ON (c:%s) ASSERT c.name IS UNIQUE", label)));
    }

    @Override
    public Set<Long> findOrphanedNodes() {
        return q("START n = NODE(*) WHERE NOT n-[*..2]->() RETURN ID(n) AS ID").stream()
                .map(row -> String.valueOf(row.get("ID")))
                .map(Long::valueOf)
                .collect(toSet());
    }

    private Collection<Map<String, Object>> q(String query) {
        return newArrayList(operations.query(query, params().build()).queryResults());
    }

    private ImmutableMap.Builder<String, Object> params() {
        return new ImmutableMap.Builder<>();
    }

    @Autowired
    public void setNeo4jOperations(Neo4jOperations operations) {
        this.operations = operations;
    }

}
