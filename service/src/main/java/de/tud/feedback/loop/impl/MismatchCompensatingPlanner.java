package de.tud.feedback.loop.impl;

import de.tud.feedback.ChangeRequest;
import de.tud.feedback.domain.ContextMismatch;
import de.tud.feedback.domain.Objective;
import de.tud.feedback.loop.MismatchProvider;
import de.tud.feedback.loop.Planner;
import de.tud.feedback.repository.graph.ObjectiveRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.lang.String.format;

@Component
public class MismatchCompensatingPlanner implements Planner {

    private static final Logger LOG = LoggerFactory.getLogger(MismatchCompensatingPlanner.class);

    private ObjectiveRepository objectiveRepository;

    private MismatchProvider mismatchProvider;

    @Override
    public void generatePlanFor(ChangeRequest changeRequest) {
        try {
            ContextMismatch mismatch = mismatchProvider.getMismatch(
                    satisfiedExpressionFrom(changeRequest), contextVariablesFrom(changeRequest));

        } catch (RuntimeException exception) {
            failOn(changeRequest, exception.getMessage());
        }
    }

    private void failOn(ChangeRequest changeRequest, String cause) {
        Objective objective = changeRequest.getObjective();
        LOG.warn(format("Compensation of %s failed. %s", objective, cause));
        objective.setState(Objective.State.FAILED);
        objectiveRepository.save(objective);
    }

    private String satisfiedExpressionFrom(ChangeRequest changeRequest) {
        return changeRequest.getObjective().getSatisfiedExpression();
    }

    private Map<String, Object> contextVariablesFrom(ChangeRequest changeRequest) {
        return changeRequest.getResult().getContextVariables();
    }

    @Autowired
    public void setObjectiveRepository(ObjectiveRepository objectiveRepository) {
        this.objectiveRepository = objectiveRepository;
    }

    @Autowired
    public void setMismatchProvider(MismatchProvider provider) {
        mismatchProvider = provider;
    }

}
