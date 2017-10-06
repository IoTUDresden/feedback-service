package de.tud.feedback.plugin.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class NeoPeerMetric {
    @GraphId
    private Long id;

    private boolean hasBattery = false;
    private Integer batteryValue;

    public NeoPeerMetric() {
    }

    public boolean hasBattery() {
        return hasBattery;
    }

    public void setHasBattery(boolean hasBattery) {
        this.hasBattery = hasBattery;
    }

    /**
     * Gets the battery value in % or null if the peer has no battery
     * @return
     */
    public Integer getBatteryValue() {
        return batteryValue;
    }

    public void setBatteryValue(Integer batteryValue) {
        this.batteryValue = batteryValue;
    }

    public Long getId() {
        return id;
    }
}
