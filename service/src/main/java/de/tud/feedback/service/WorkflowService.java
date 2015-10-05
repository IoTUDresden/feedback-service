package de.tud.feedback.service;

import de.tud.feedback.domain.WorkflowInstance;

public interface WorkflowService {

    void attendExecutionOf(WorkflowInstance instance);

}
