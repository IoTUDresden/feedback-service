package de.tud.feedback.loop.impl;

import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.ObjectiveEvaluator;
import de.tud.feedback.domain.Goal;
import de.tud.feedback.domain.Objective;
import de.tud.feedback.loop.Analyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.joda.time.DateTime.now;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DelegatingAnalyzer implements Analyzer {

    private static final Logger LOG = LoggerFactory.getLogger(DelegatingAnalyzer.class);

    private FeedbackPlugin plugin;

    private List<ObjectiveEvaluator> evaluators;

    @PostConstruct
    public void initialize() {
        evaluators = newArrayList(plugin.getObjectiveEvaluators());
        evaluators.sort(AnnotationAwareOrderComparator.INSTANCE);
    }

    @Override
    public boolean analyze(Collection<Goal> goals) {
        return goals.stream()
                .filter(goal -> !goal.hasBeenSatisfied())
                .filter(this::evaluate)
                .allMatch(Goal::hasBeenSatisfied);
    }

    private boolean evaluate(Goal goal) {
        return goal.getObjectives().stream()
                .filter(objective -> !objective.hasBeenSatisfied())
                .filter(this::evaluate)
                .allMatch(Objective::hasBeenSatisfied);
    }

    private boolean evaluate(Objective objective) {
        Optional<ObjectiveEvaluator> compatibleEvaluator = evaluators.stream()
                .filter(evaluator -> evaluator.getSupportedMimeTypes().contains(objective.getMime()))
                .findFirst();

        if (compatibleEvaluator.isPresent() && compatibleEvaluator.get().evaluate(objective)) {
            objective.setSatisfaction(now());
            return true;

        } else {
            LOG.error(format("There's no evaluator for mime type '%s'", objective.getMime()));
            return false;
        }
    }

    @Autowired
    public void setFeedbackPlugin(FeedbackPlugin plugin) {
        this.plugin = plugin;
    }

}
