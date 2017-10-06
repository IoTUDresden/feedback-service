//TODO check peers for same devices, heartbeats and battery?
Match(original:NeoProcess)
WHERE ID(original) = {processNodeId}
WITH original

Match(remote:NeoProcess)-[remoteFor:REMOTE_FOR]->(original)
WITH remote, original

Match(original)-[runsOnSuper:RUNS_ON]->(originalPeer:NeoPeer)
WITH originalPeer, remote, original

Match(remote)-[runsOnRemote:RUNS_ON]->(remotePeer:NeoPeer)
WITH remotePeer, originalPeer, remote, original

MATCH (newPeer:NeoPeer)
WHERE newPeer <> originalPeer AND newPeer <> remotePeer

RETURN original, remotePeer, newPeer
