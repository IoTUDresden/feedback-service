package de.tud.feedback.plugin.repository;

import de.tud.feedback.plugin.domain.NeoPeer;
import de.tud.feedback.plugin.domain.NeoProcess;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Process Repository for processes executed by a process engine.
 *
 * See <a href=https://projects.spring.io/spring-data-neo4j>Example/Tutorial</a>
 *
 * http://docs.spring.io/spring-data/jpa/docs/1.5.0.RELEASE/reference/html/repositories.html
 */
@Repository
public interface NeoProcessRepository extends GraphRepository<NeoProcess> {

    //TODO check if this is working without the cypher query
    //@Query("MATCH (p:NeoProcess {processId={0}} ) RETURN p")
    NeoProcess findByProcessId(String processId);

    /**
     * finds all processes, which are running on the given peer
     * @param peer
     * @return
     */
    List<NeoProcess> findByPeer(NeoPeer peer);

    //the autogeneration will not work here :(
    @Query("MATCH (n:NeoProcess) WHERE NOT HAS(n.peer) RETURN n")
    List<NeoProcess> findByPeerIsNull();

//    /**
//     * Updates the process state with the given id.
//     * @param id
//     * @param state
//     */
//    @Query("MATCH (p:NeoProcess {processId={0}} )" +
//            "SET p.state = {1}")
//    void updateProcessState(String id, String state);
}
