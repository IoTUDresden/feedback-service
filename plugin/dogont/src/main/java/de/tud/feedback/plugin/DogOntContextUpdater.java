package de.tud.feedback.plugin;

import de.tud.feedback.ContextUpdater;
import de.tud.feedback.CypherExecutor;
import de.tud.feedback.annotation.LogInvocation;
import de.tud.feedback.annotation.LogTimeSeries;
import de.tud.feedback.domain.Context;

import java.util.Map;
import java.util.function.Function;

import static de.tud.feedback.Utils.params;
import static java.util.stream.Collectors.toMap;

public class DogOntContextUpdater implements ContextUpdater {

    private final Function<String, String> stateNameMapper;

    private final CypherExecutor executor;

    private Context context;

    private Map<String, Integer> stateValueMapping;
    private Map<String, Integer> peerValueMapping;
    private Listener listener;

    public DogOntContextUpdater(CypherExecutor executor, Function<String, String> stateNameMapper) {
        this.stateNameMapper = stateNameMapper;
        this.executor = executor;
    }

    @Override
    @LogInvocation
    @LogTimeSeries(context = "context.#{context.name}")
    public void update(String item, Object state) {
        //FIXME HACK:
        if (item.toLowerCase().contains("peer"))
        {
            if (state instanceof String){
                String process = (String) state;
                if (process.toLowerCase().contains("process"))
                {
                    updatePeer(item,process);
                    return;
                }
            }
        }

        final String stateName = stateNameMapper.apply(item);

        if (stateValueMapping == null) {
            stateValueMapping = resolveStateValueMapping();
        }

        if (stateValueMapping.containsKey(stateName)) {
            executor.execute(
                    "MATCH (v) " +
                    "WHERE ID(v) = {id} " +
                    "SET v.realStateValue = {value} " +
                    "RETURN v",

                    params().put("id", stateValueMapping.get(stateName))
                            .put("value", state)
                            .build());

            listener.contextUpdated();
        }
    }

    private Map<String, Integer> resolveStateValueMapping() {
        return executor.execute(
                    "MATCH (thing)-[:within]->(import:ContextImport) " +
                    "MATCH (import)-[:for]->(context:Context) " +
                    "MATCH (thing)-[:hasState]->(state) " +
                    "MATCH (state)-[:hasStateValue]->(value) " +
                    "WHERE context.name = {contextName} " +
                    "RETURN state.name AS state, ID(value) AS valueId",

                    params().put("contextName", context.getName())
                            .build())

                .stream()
                .collect(toMap(
                        e -> (String) e.get("state"),
                        e -> (Integer) e.get("valueId")));
    }

    /**
     * updates peer to process relation, deletes old peer to process relation
     * @param item
     * @param state
     */
    public void updatePeer(String item, Object state) {
        final String stateName = item;
        System.out.println("Peer: "+ stateName + " -> "+state);

        peerValueMapping = resolvePeerValueMapping();


        if (peerValueMapping.containsKey(stateName)) {
            executor.execute(
                    "MATCH (v)-[:type]->(:Class{name:'Peer'}) " +
                            "MATCH (oldPeer)-[:hasProcess]->(process) " +
                            "MATCH (oldPeer)-[r]->(process) " +
                            "WHERE ID(v) = {id} AND process.name = {value} " +
                            "CREATE (v)-[:hasProcess]->(process) " +
                            "DELETE r " +
                            "RETURN v",

                    params().put("id", peerValueMapping.get(stateName))
                            .put("value", state)
                            .build());

            listener.contextUpdated();
        }
    }

    private Map<String, Integer> resolvePeerValueMapping() {
        return executor.execute(
                "MATCH (thing)-[:within]->(import:ContextImport) " +
                        "MATCH (import)-[:for]->(context:Context) " +
                        "MATCH (thing)-[:type]->(:Class{name:'Peer'})" +
                        "OPTIONAL MATCH (thing)-[:hasProcess]->(process) " +
                        "WHERE context.name = {contextName} " +
                        "RETURN thing.name AS state, ID(thing) AS valueId",

                params().put("contextName", context.getName())
                        .build())

                .stream()
                .collect(toMap(
                        e -> (String) e.get("state"),
                        e -> (Integer) e.get("valueId")));
    }
    @Override
    public void workWith(Context context) {
        this.context = context;
    }

    @Override
    public void workWith(Listener listener) {
        this.listener = listener;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
