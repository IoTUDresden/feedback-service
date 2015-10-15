package de.tud.feedback.loop;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Goal;

import java.util.Collection;

public interface Analyzer {

    void analyze(Collection<Goal> goals, Context context);

}
