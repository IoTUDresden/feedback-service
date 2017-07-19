//TODO check peers for same devices, heartbeats and battery?
Match(process:NeoProcess)-[runsOn:RUNS_ON]->(executingPeer:NeoPeer)
WHERE ID(process) = {processNodeId}
WITH process, executingPeer

Match(peer:NeoPeer)
WHERE peer <> executingPeer

RETURN process, peer, executingPeer
