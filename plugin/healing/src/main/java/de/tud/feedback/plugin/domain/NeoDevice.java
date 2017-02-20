package de.tud.feedback.plugin.domain;

import org.neo4j.ogm.annotation.GraphId;

public class NeoDevice {

    @GraphId
    private Long id;
    private String deviceId;

    public Long getId() {
        return id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
