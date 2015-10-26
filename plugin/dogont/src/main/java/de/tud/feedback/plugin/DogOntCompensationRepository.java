package de.tud.feedback.plugin;

import com.google.common.collect.ImmutableMap;
import de.tud.feedback.CypherExecutor;
import de.tud.feedback.domain.Command;
import de.tud.feedback.repository.CompensationRepository;

import java.util.Collection;
import java.util.Map;

import static de.tud.feedback.Utils.params;
import static java.util.stream.Collectors.toSet;

public class DogOntCompensationRepository implements CompensationRepository {

    private static final String QUERY = 
            "MATCH (sensor)-[:hasState]->(sensorState) " +
            "MATCH (actuator)-[:hasState]->(actuatorState) " +
            "MATCH (sensor)-[:isIn]->(room)<-[:isIn]-(actuator) " +
            "MATCH (sensorState)-[:type]->()-[:subClassOf*0..1]->()<-[:type]-(actuatorState) " +
            "MATCH (actuator)-[:hasFunctionality]->(function)-[:hasCommand]->(command) " +
            "MATCH (command)-[:type]->(commandType) " +
            "MATCH (function)-[:type]->()-[:subClassOf*]->({ name: 'ControlFunctionality' }) " +
            "WHERE id(sensorState) = {stateId} AND has(command.realCommandName) " +
            "RETURN actuator.name AS actuator, " +
                    "commandType.name AS commandType, " +
                    "command.realCommandName AS commandName";

    private static final Map<String, Command.Type> commandTypes = ImmutableMap.<String, Command.Type>builder()
            .put("OnCommand",   Command.Type.ASSIGN)
            .put("OffCommand",  Command.Type.ASSIGN)
            .put("UpCommand",   Command.Type.UP)
            .put("DownCommand", Command.Type.DOWN)
            .build();

    private final CypherExecutor executor;

    public DogOntCompensationRepository(CypherExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Collection<Command> findCommandsManipulating(Long testNodeId) {
        return executor.execute(QUERY, params()
                .put("stateId", testNodeId)
                    .build())
                .stream()
                .map(this::toCommand)
                .collect(toSet());
    }

    private Command toCommand(Map<String, Object> objectMap) {
        //noinspection SuspiciousMethodCalls
        return new Command()
                .setTargetTo((String) objectMap.get("actuator"))
                .setNameTo((String) objectMap.get("commandName"))
                .setTypeTo(commandTypes.get(objectMap.get("commandType")));
    }

}
