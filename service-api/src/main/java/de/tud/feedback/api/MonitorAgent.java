package de.tud.feedback.api;

public interface MonitorAgent extends Runnable {

    void workWith(ContextUpdater updater);

}
