package de.tud.feedback.loop;

import de.tud.feedback.ChangeRequest;

public interface Planner {

    void queue(ChangeRequest changeRequest);

}
