package de.tud.feedback.service;

import de.tud.feedback.domain.Workflow;
import de.tud.feedback.domain.WorkflowInstance;

public interface WorkflowService {

    void attendExecutionOf(WorkflowInstance instance);

    String augment(Workflow workflow);

}
