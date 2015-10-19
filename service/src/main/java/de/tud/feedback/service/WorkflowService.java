package de.tud.feedback.service;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Workflow;

public interface WorkflowService {

    void analyzeGoalsForWorkflowsWithin(Context context);

    void analyzeGoalsFor(Workflow workflow);

}
