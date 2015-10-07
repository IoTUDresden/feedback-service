package de.tud.feedback;

public interface MonitorAgent extends Runnable {

    void workWith(ContextUpdater updater);

}
