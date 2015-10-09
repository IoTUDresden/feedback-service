package de.tud.feedback.plugin.factory;

import de.tud.feedback.CypherExecutor;
import de.tud.feedback.plugin.DogOntContextUpdater;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class DogOntContextUpdaterFactoryBean extends AbstractFactoryBean<DogOntContextUpdater> {

    private CypherExecutor executor;

    private String stateNodePrefix = "";

    @Override
    protected DogOntContextUpdater createInstance() throws Exception {
        checkNotNull(executor, "CypherExecutor is missing");

        return new DogOntContextUpdater(executor, stateNodePrefix);
    }

    public DogOntContextUpdaterFactoryBean setExecutor(CypherExecutor executor) {
        this.executor = checkNotNull(executor);
        return this;
    }

    public DogOntContextUpdaterFactoryBean setStateNodePrefix(String stateNodePrefix) {
        this.stateNodePrefix = checkNotNull(stateNodePrefix);
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
