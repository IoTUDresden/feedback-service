package de.tud.feedback.plugin;

import de.tud.feedback.CypherExecutor;
import de.tud.feedback.domain.Command;
import de.tud.feedback.repository.CommandRepository;

import java.util.Collection;
import java.util.Map;

import static de.tud.feedback.Utils.params;
import static java.util.stream.Collectors.toSet;

public class DogOntCommandRepository implements CommandRepository {

    private static final String QUERY = 
            "MATCH (sensor)-[:hasState]->(sensorState) " +
            "MATCH (actuator)-[:hasState]->(actuatorState) " +
            "MATCH (sensor)-[:isIn]->(room)<-[:isIn]-(actuator) " +
            "MATCH (sensorState)-[:type]->()-[:subClassOf*0..1]->()<-[:type]-(actuatorState) " +
            "MATCH (actuator)-[:hasFunctionality]->(function)-[:hasCommand]->(command) " +
            "MATCH (command)-[:type]->(commandType) " +
            "MATCH (function)-[:type]->()-[:subClassOf*]->({ name: 'ControlFunctionality' }) " +
            "WHERE ID(sensorState) = {stateId} " +
            "RETURN actuator.name AS actuatorName, " +
                    "command.realCommandName AS commandName, " +
                    "commandType.name AS commandType";

    private final CypherExecutor executor;

    public DogOntCommandRepository(CypherExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Collection<Command> findCommandsManipulating(Long testNodeId) {
        return executor.execute(QUERY, params()
                    .put("stateId", testNodeId)
                    .build())
                .stream()
                .map(this::toCommand)
                .filter(command -> command != null)
                .collect(toSet());
    }

    private Command toCommand(Map<String, Object> objectMap) {
        return new Command()
                .setNameTo(String.valueOf(objectMap.get("actuatorName")))
                .setTargetTo(String.valueOf(objectMap.get("commandName")))
                .setTypeTo(toCommandType(String.valueOf(objectMap.get("commandType"))));
    }

    private Command.Type toCommandType(String commandType) {
        return null;
    }

}
