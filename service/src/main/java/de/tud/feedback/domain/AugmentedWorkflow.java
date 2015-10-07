package de.tud.feedback.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = Workflow.class, name = "augmented")
public interface AugmentedWorkflow {

    @Value("#{@workflows.augment(target)}")
    String getAugmented();

}
