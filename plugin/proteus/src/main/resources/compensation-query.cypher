//TODO only for testing return all peers
MATCH (peer:NeoPeer)

RETURN DISTINCT
    peer.peerId AS peerId,
    peer.ip AS ip
