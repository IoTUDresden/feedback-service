package de.tud.feedback;

import de.tud.feedback.domain.Context;

public interface WorkflowAssertion {

    boolean hasTimedOut();

    boolean hasBeenMetWithin(Context context);

}
