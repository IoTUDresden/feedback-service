package de.tud.feedback.loop;

import de.tud.feedback.api.FeedbackService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(FeedbackService.PROCESS_EXECUTION)
public class Analyze {
}
