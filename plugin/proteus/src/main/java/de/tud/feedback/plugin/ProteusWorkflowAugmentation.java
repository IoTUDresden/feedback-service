package de.tud.feedback.plugin;

import de.tud.feedback.WorkflowAugmentation;
import de.tud.feedback.domain.Workflow;
import org.springframework.stereotype.Component;

@Component
public class ProteusWorkflowAugmentation implements WorkflowAugmentation {

    @Override
    public String augment(Workflow workflow) {
        return "augmented"; // TODO
    }

}
