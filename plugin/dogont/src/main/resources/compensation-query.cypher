// Lookup commands influencing a reality value
// -------------------------------------------

// Sensor has got a state
MATCH (sensor)-[:hasState]->(sensorState)

// Actuator has got a state
MATCH (actuator)-[:hasState]->(actuatorState)

// Actuator has got a value
MATCH (actuatorState)-[:hasStateValue]->(actuatorStateValue)

// This value has a specific unit
MATCH (actuatorStateValue)-[:unitOfMeasure]->(actuatorStateUnit)

// Sensor and actuator are in the same room
MATCH (sensor)-[:isIn]->(room)<-[:isIn]-(actuator)

// State type for sensor and actuator are equal up to one level in the type hierarchy
MATCH (sensorState)-[:type]->()-[:subClassOf*0..1]->()<-[:type]-(actuatorState)

// Actuator has functionality with command
MATCH (actuator)-[:hasFunctionality]->(function)-[:hasCommand]->(command)

// Command has got a type
MATCH (command)-[:type]->(commandType)

// Actuator has control functionality
MATCH (function)-[:type]->()-[:subClassOf*]->({ name: "ControlFunctionality" })

// Restrict on a state and non null attributes
WHERE id(sensorState) = {stateId}
    AND has(command.realCommandName)
    AND has(commandType.name)

// Attributes needed for compensating command
RETURN DISTINCT
	actuator.name AS actuator,
	commandType.name AS commandType,
	command.realCommandName AS commandName,
	actuatorStateValue.realStateValue AS actuatorState,
	actuatorStateUnit.name AS actuatorStateUnit