package de.tud.feedback.plugin.factory;

import de.tud.feedback.CypherExecutor;
import de.tud.feedback.plugin.DogOntObjectiveEvaluator;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class DogOntObjectiveEvaluatorFactoryBean extends AbstractFactoryBean<DogOntObjectiveEvaluator> {

    private CypherExecutor executor;

    @Override
    protected DogOntObjectiveEvaluator createInstance() throws Exception {
        checkNotNull(executor, "CypherExecutor is missing");

        return new DogOntObjectiveEvaluator(executor);
    }

    public DogOntObjectiveEvaluatorFactoryBean setExecutor(CypherExecutor executor) {
        this.executor = checkNotNull(executor);
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return DogOntObjectiveEvaluator.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
