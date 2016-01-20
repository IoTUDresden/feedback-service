package de.tud.feedback.domain;

import de.tud.feedback.Satisfiable;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.lang.String.format;

public class ObjectiveEvaluationResult implements Satisfiable {

    private boolean compensate = false;

    private boolean satisfied = false;

    private Map<String, Object> contextVariables = newHashMap();

    private Long testNodeId;

    public static ObjectiveEvaluationResult build() {
        return new ObjectiveEvaluationResult();
    }

    public ObjectiveEvaluationResult setCompensateTo(boolean compensate) {
        this.compensate = compensate;
        return this;
    }

    public ObjectiveEvaluationResult setSatisfiedTo(boolean satisfied) {
        this.satisfied = satisfied;
        return this;
    }

    public ObjectiveEvaluationResult setTestNodeIdTo(Long testNodeIdTo) {
        this.testNodeId = testNodeIdTo;
        return this;
    }

    public ObjectiveEvaluationResult setContextVariables(Map<String, Object> contextVariables) {
        this.contextVariables = contextVariables;
        return this;
    }

    public Map<String, Object> getContextVariables() {
        return contextVariables;
    }

    public Long getTestNodeId() {
        return testNodeId;
    }

    public boolean shouldBeCompensated() {
        return compensate;
    }

    @Override
    public boolean hasBeenSatisfied() {
        return satisfied;
    }

    @Override
    public String toString() {
        return format("%s(compensate = %s, satisfied = %s)", getClass().getSimpleName(), compensate, satisfied);
    }

}
