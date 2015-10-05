package de.tud.feedback.graph;

import de.tud.feedback.api.CypherExecutor;
import org.neo4j.ogm.session.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class NodeCollectingCypherExecutor implements CypherExecutor {

    private final Set<Long> nodes = newHashSet();

    private final Neo4jOperations operations;

    @Autowired
    public NodeCollectingCypherExecutor(Neo4jOperations operations) {
        this.operations = operations;
    }

    public Set<Long> createdNodes() {
        return nodes;
    }

    @Override
    public Iterable<Map<String, Object>> execute(String cypherQuery, Map<String, ?> params) {
        Result result = operations.query(cypherQuery, params);

        if (hasCreatedNodes(result)) {
            nodes.addAll(createdNodesFor(idsWithin(result)));
        }

        return result.queryResults();
    }

    private Collection<Long> createdNodesFor(List<Integer> ids) {
        return ids.stream()
                .map(Integer::longValue)
                .collect(toList());
    }

    private List<Integer> idsWithin(Result result) {
        List<Integer> ids = newArrayList();

        rowsFrom(result).forEach(row ->
                row.keySet().stream().filter(key -> key.startsWith("ID(")).forEach(idKey ->
                        ids.add((Integer) row.get(idKey))));

        // Due to the merge operation, there might be more creations reported, than actually made.
        // This is not a problem, since we're collecting IDs of created nodes in a set.
        // But too few reported creations can lead to untracked IDs.
        if (ids.size() < numberOfCreatedNodesFrom(result)) {
            throw new RuntimeException("More generated nodes than reported");
        }

        return ids;
    }

    private Set<Map<String, Object>> rowsFrom(Result result) {
        return newHashSet(result.queryResults());
    }

    private int numberOfCreatedNodesFrom(Result result) {
        return result.queryStatistics().getNodesCreated();
    }

    private boolean hasCreatedNodes(Result result) {
        return numberOfCreatedNodesFrom(result) > 0;
    }

}
