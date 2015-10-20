package de.tud.feedback.domain;

import de.tud.feedback.Satisfiable;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

public class ObjectiveEvaluationResult implements Satisfiable {

    private boolean compensate = false;

    private boolean satisfied = false;

    private Collection<ContextMismatch> mismatches = newArrayList();

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

    public ObjectiveEvaluationResult setMismatchesTo(Collection<ContextMismatch> mismatches) {
        try {
            this.mismatches = checkNotNull(mismatches);
        } catch (NullPointerException exception) {
            this.mismatches = newHashSet();
        }

        return this;
    }

    public Collection<ContextMismatch> getMismatches() {
        return mismatches;
    }

    public boolean shouldBeCompensated() {
        return compensate;
    }

    @Override
    public boolean hasBeenSatisfied() {
        return satisfied;
    }

}
