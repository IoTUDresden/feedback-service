package de.tud.feedback;

import de.tud.feedback.domain.Workflow;

public interface WorkflowAugmentation {

    String CACHE = "augmentedWorkflows";

    String augment(Workflow workflow);

}
