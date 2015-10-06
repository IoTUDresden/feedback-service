package de.tud.feedback.api;

public interface MonitorAgent extends Runnable {

    void use(ContextUpdater updater);

}
