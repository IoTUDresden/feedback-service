package de.tud.feedback.plugin.factory;

import de.tud.feedback.CypherExecutor;
import de.tud.feedback.plugin.SpelObjectiveEvaluator;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class SpelObjectiveEvaluatorFactoryBean extends AbstractFactoryBean<SpelObjectiveEvaluator> {

    private CypherExecutor executor;

    @Override
    protected SpelObjectiveEvaluator createInstance() throws Exception {
        checkNotNull(executor, "CypherExecutor is missing");

        return new SpelObjectiveEvaluator(executor);
    }

    public SpelObjectiveEvaluatorFactoryBean setExecutor(CypherExecutor executor) {
        this.executor = checkNotNull(executor);
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return SpelObjectiveEvaluator.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
