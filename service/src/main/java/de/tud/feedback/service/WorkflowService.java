package de.tud.feedback.service;

import de.tud.feedback.domain.Workflow;

public interface WorkflowService {

    void attend(Workflow workflow);

    void deleteGoals(Workflow workflow);

}
