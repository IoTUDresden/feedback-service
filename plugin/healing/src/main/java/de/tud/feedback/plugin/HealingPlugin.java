package de.tud.feedback.plugin;

import de.tud.feedback.plugin.repository.NeoDeviceRepository;
import de.tud.feedback.plugin.repository.NeoPeerMetricRepository;
import de.tud.feedback.plugin.repository.NeoPeerRepository;
import de.tud.feedback.plugin.repository.NeoProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Provider;


@Component
public class HealingPlugin {

    @Autowired
    private NeoPeerRepository peerRepository;
    @Autowired
    private NeoProcessRepository processRepository;
    @Autowired
    private NeoPeerMetricRepository metricRepository;
    @Autowired
    private NeoDeviceRepository deviceRepository;

    public NeoPeerRepository getNeoPeerRepository() {
        return peerRepository;
    }

    public NeoProcessRepository getNeoProcessRepository() {
        return processRepository;
    }

    public NeoPeerMetricRepository getNeoPeerMetricRepository() { return metricRepository; }

    public NeoDeviceRepository getNeoDeviceRepository() {  return deviceRepository;  }

}
