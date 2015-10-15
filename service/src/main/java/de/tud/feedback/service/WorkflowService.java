package de.tud.feedback.service;

import de.tud.feedback.domain.Context;
import de.tud.feedback.domain.Workflow;

public interface WorkflowService {

    void deleteGoalsFor(Workflow workflow);

    void analyzeGoalsFor(Workflow workflow);

    void analyzeGoalsForWorkflowsWithin(Context context);

}
