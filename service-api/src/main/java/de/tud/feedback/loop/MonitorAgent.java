package de.tud.feedback.loop;

import de.tud.feedback.ContextUpdater;

public interface MonitorAgent extends Runnable {

    void workWith(ContextUpdater updater);

}
