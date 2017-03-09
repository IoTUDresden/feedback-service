package de.tud.feedback.plugin.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This represents a peer in a distributed process environment
 */
@NodeEntity
public class NeoPeer {

    @GraphId
    private Long id;

    @Index(unique=true)
    private String peerId;
    private String name;
    private String ip;
    private boolean isSuperPeer;

    @Relationship(type="HAS_METRIC", direction=Relationship.OUTGOING)
    private NeoPeerMetric metrics;

    @Relationship(type="HAS_DEVICE", direction=Relationship.OUTGOING)
    private Set<NeoDevice> devices = new HashSet<>();

    //java 8 LocalDatetime is not serialized by spring data neo4j
    private Date lastHeartbeat;
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
    public Date getLastHeartbeat() {
        return lastHeartbeat;
    }

    /**
     * the date of the last heartbeat from this peer
     */
    public void setLastHeartbeat(Date lastHeartbeat) {
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

    public NeoPeerMetric getMetrics() {
        return metrics;
    }

    public void setMetrics(NeoPeerMetric metrics) {
        this.metrics = metrics;
    }

    public void addDevice(NeoDevice device){
        boolean alreadyExists = devices.stream().anyMatch(d -> d.getDeviceId().equals(device.getDeviceId()));
        if(!alreadyExists)
            devices.add(device);
    }

    public void removeDevice(String deviceId){
        devices.removeAll(
                devices.stream()
                        .filter(d -> d.getDeviceId().equals(deviceId))
                        .collect(Collectors.toList()));
    }
}
