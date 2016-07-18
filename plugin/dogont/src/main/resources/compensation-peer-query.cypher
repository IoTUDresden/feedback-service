// Lookup peers to run the process alternatively
// -------------------------------------------
// Search for current process executing peer
MATCH (oldPeer)-[:hasState]->(oldPeerState)
// get the process
MATCH (oldPeer)-[:hasProcess]->(process)
MATCH (newPeer)-[:hasState]->(newPeerState)
// find new peers with similar controlled devices
MATCH (newPeer)-[:hasDevices]->(devices)<-[:hasDevices]-(oldPeer)
// find peer with similar functionality
MATCH (newPeer)-[:hasFunctionality]->(functionality)<-[:hasFunctionality]-(oldPeer)
// find peer with similar state types
MATCH (oldPeerState)-[:type]->(stateType)<-[:type]-(newPeerState)
// find out commands the peer is supporting
MATCH (newPeer)-[:hasFunctionality]->(function)-[:hasCommand]->(command)
MATCH (command)-[:type]->(commandType)
// measurement point is the process, use peer with no currently running process
WHERE id(process) = {stateId} AND NOT (newPeer)-[:hasProcess]->() AND has(commandType.name)
//Return data of new Peer and its supported commands
RETURN DISTINCT
    oldPeer.name AS oldPeer,
	newPeer.name AS newPeer,
	newPeer.IPAddress AS ipAddress,
	process.name AS processName,
	commandType.name AS commandType,
	command.realCommandName AS commandName