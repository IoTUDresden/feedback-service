package de.tud.feedback.plugin.factory;

import de.tud.feedback.api.CypherExecutor;
import de.tud.feedback.plugin.DogOntContextUpdater;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class DogOntContextUpdaterFactoryBean extends AbstractFactoryBean<DogOntContextUpdater> {

    private CypherExecutor executor;

    public static DogOntContextUpdaterFactoryBean build() {
        return new DogOntContextUpdaterFactoryBean();
    }

    public void setExecutor(CypherExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Class<?> getObjectType() {
        return DogOntContextUpdater.class;
    }

    @Override
    protected DogOntContextUpdater createInstance() throws Exception {
        return new DogOntContextUpdater(executor);
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
