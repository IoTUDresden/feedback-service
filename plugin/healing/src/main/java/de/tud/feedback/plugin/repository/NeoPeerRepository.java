package de.tud.feedback.plugin.repository;

import de.tud.feedback.plugin.domain.NeoPeer;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Graph Repo for manage all available (or none) available peers.
 */
@Repository
public interface NeoPeerRepository extends GraphRepository<NeoPeer> {

    /**
     * gets the peer by its peer id
     * @param peerId
     * @return
     */
    NeoPeer findByPeerId(String peerId);

    /**
     * finds a peer by the given ip
     * @param ip
     * @return
     */
    NeoPeer findByIp(String ip);

    /**
     * Deletes the peer and the metrics
     * @param peerId
     */
    @Query("MATCH (peer:NeoPeer{peerId:{peerId}})-[hasm:HAS_METRIC]->(metric:NeoPeerMetric) DELETE peer, hasm, metric")
    void delete(@Param("peerId") String peerId);



}
