package de.tud.feedback.plugin;

import de.tud.feedback.api.ContextUpdater;
import de.tud.feedback.api.MonitorAgent;
import de.tud.feedback.plugin.openhab.OpenHabWebSocketHandler;

public class OpenHabMonitorAgent implements MonitorAgent {

    private final String host;

    private final Integer port;

    public OpenHabMonitorAgent(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void start(ContextUpdater updater) {
        OpenHabWebSocketHandler handler = new OpenHabWebSocketHandler();
        //client.doHandshake(handler, getWebSocketUrl());
    }

    private String getWebSocketUrl() {
        return "ws://example.com:8080/";
    }

    @Override
    public void stop() {

    }

    /*public void connect(RealityChangeHandler updateConsumer) {
        OpenHabWebSocketHandler handler = new OpenHabWebSocketHandler(updateConsumer);
        // TODO
    }*/

}
