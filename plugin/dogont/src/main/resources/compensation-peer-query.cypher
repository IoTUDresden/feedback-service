// Lookup peers to run the process alternatively
// -------------------------------------------
MATCH (oldPeer)-[:hasState]->(oldPeerState)
MATCH (oldPeer)-[:hasProcess]->(process)
MATCH (newPeer)-[:hasState]->(newPeerState)
MATCH (newPeer)-[:hasState]->(newPeerState2)
//MATCH (newPeerState2)-[:hasStateValue]->(newPeerStateValue2)
MATCH (newPeerState)-[:hasStateValue]->(newPeerStateValue)
MATCH (newPeer)-[:hasDevices]->(devices)<-[:hasDevices]-(oldPeer)
MATCH (newPeer)-[:hasFunctionality]->(functionality)<-[:hasFunctionality]-(oldPeer)
MATCH (oldPeerState)-[:type]->(stateType)<-[:type]-(newPeerState2)
//MATCH (oldPeerState)-[:type]->()-[:subClassOf*0..1]->()<-[:type]-(newPeerState2)
MATCH (newPeerStateValue)-[:type]->({ name: "NoFailureStateValue" })
MATCH (newPeer)-[:hasFunctionality]->(function)-[:hasCommand]->(command)
MATCH (command)-[:type]->(commandType)
MATCH (newPeer)-[:isIn]->(room)<-[:isIn]-(oldPeer)
WHERE id(process) = {stateId} AND NOT (newPeer)-[:hasProcess]->() AND has(commandType.name)
RETURN DISTINCT
	newPeer.name AS newPeer,
	newPeer.IPAddress AS ipAddress,
	commandType.name AS commandType,
	command.realCommandName AS commandName