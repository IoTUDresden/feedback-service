package de.tud.feedback.loop;

import de.tud.feedback.domain.Goal;

import java.util.Collection;

public interface Analyzer {

    boolean evaluate(Collection<Goal> goals);

}
