package de.tud.feedback.plugin.repository;

import de.tud.feedback.plugin.domain.NeoPeer;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Graph Repo for manage all available (or none) available peers.
 */
@Repository
public interface NeoPeerRepository extends GraphRepository<NeoPeer> {

//    //TODO test - does this return null, if no peer was found under this id? And if this updates
//    // maybe better just to get the peer by id, then change, then save
//    /**
//     * updates the time of the last heartbeat from a given peer
//     * @param peerId
//     * @param time
//     * @return the peer which was updated
//     */
//    @Query("MATCH (p:NeoPeer {peerId={0}} )" +
//            "SET p.lastHeartbeat = {1}" +
//            "RETURN p")
//    NeoPeer updateHeartbeatDateOf(String peerId, Date time);


    //TODO check if this is working without the cypher query
    /**
     * gets the peer by its peer id
     * @param peerId
     * @return
     */
    //@Query("MATCH (p:NeoPeer {peerId={0}}) RETURN p")
    NeoPeer findByPeerId(String peerId);

    /**
     * finds a peer by the given ip
     * @param ip
     * @return
     */
    NeoPeer findByIp(String ip);



}
