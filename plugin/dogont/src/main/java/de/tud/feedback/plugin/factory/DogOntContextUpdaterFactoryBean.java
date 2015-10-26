package de.tud.feedback.plugin.factory;

import de.tud.feedback.CypherExecutor;
import de.tud.feedback.plugin.DogOntContextUpdater;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

public class DogOntContextUpdaterFactoryBean extends AbstractFactoryBean<DogOntContextUpdater> {

    private CypherExecutor executor;

    private Function<String, String> stateNameMapper = s -> s;

    @Override
    protected DogOntContextUpdater createInstance() throws Exception {
        checkNotNull(executor, "CypherExecutor is missing");

        return new DogOntContextUpdater(executor, stateNameMapper);
    }

    public DogOntContextUpdaterFactoryBean setExecutor(CypherExecutor executor) {
        this.executor = checkNotNull(executor);
        return this;
    }

    public DogOntContextUpdaterFactoryBean setStateNameMapper(Function<String, String> stateNameMapper) {
        this.stateNameMapper = checkNotNull(stateNameMapper);
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return DogOntContextUpdater.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
