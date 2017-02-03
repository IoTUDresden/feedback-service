package de.tud.feedback.plugin.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * This represents a peer in a distributed process environment
 */
@NodeEntity
public class NeoPeer {

    @GraphId
    private Long id;
    private String peerId;
    private String name;
    private String ip;
    private boolean isSuperPeer;

    //TODO check if this can be used as value or we must switch to Date
    private LocalDateTime lastHeartbeat;
    private boolean isConnected;

    /**
     * created by the framework
     *
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     * The peerId can also be null, if this is a superpeer
     */
    public String getPeerId() {
        return peerId;
    }

    /**
     * The peerId can also be null, if this is a superpeer
     */
    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    /**
     * The host name of the peer
     */
    public String getName() {
        return name;
    }

    /**
     * The host name of the peer
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The host ip of the peer
     */
    public String getIp() {
        return ip;
    }

    /**
     * The host ip of the peer
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isSuperPeer() {
        return isSuperPeer;
    }

    public void setSuperPeer(boolean superPeer) {
        isSuperPeer = superPeer;
    }

    /**
     * the date of the last heartbeat from this peer
     */
    public LocalDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }

    /**
     * the date of the last heartbeat from this peer
     */
    public void setLastHeartbeat(LocalDateTime lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    /**
     * state if the peer is connected or not
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * state if the peer is connected or not
     */
    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
