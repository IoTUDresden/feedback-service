package de.tud.feedback.plugin.context;

import de.tud.feedback.api.FeedbackService;
import de.tud.feedback.api.context.ContextUpdateStrategy;
import de.tud.feedback.api.graph.CypherExecutor;
import org.springframework.stereotype.Component;

@Component(FeedbackService.PROCESS_RUNNING_SCOPE)
public class OpenHabContextUpdateStrategy implements ContextUpdateStrategy {

    @Override
    public void updateContextWith(CypherExecutor executor) {

    }

}