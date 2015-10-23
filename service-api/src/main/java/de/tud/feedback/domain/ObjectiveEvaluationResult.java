package de.tud.feedback.domain;

import de.tud.feedback.Satisfiable;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class ObjectiveEvaluationResult implements Satisfiable {

    private boolean compensate = false;

    private boolean satisfied = false;

    private Map<String, Object> contextVariables = newHashMap();

    private Long measuringNodeId;

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

    public ObjectiveEvaluationResult setMeasuringNodeIdTo(Long testNodeIdTo) {
        this.measuringNodeId = testNodeIdTo;
        return this;
    }

    public ObjectiveEvaluationResult setContextVariables(Map<String, Object> contextVariables) {
        this.contextVariables = contextVariables;
        return this;
    }

    public Map<String, Object> getContextVariables() {
        return contextVariables;
    }

    public Long getMeasuringNodeId() {
        return measuringNodeId;
    }

    public boolean shouldBeCompensated() {
        return compensate;
    }

    @Override
    public boolean hasBeenSatisfied() {
        return satisfied;
    }

}
