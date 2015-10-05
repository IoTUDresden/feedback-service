package de.tud.feedback.plugin.factory;

import de.tud.feedback.api.CypherExecutor;
import de.tud.feedback.plugin.DogOntContextUpdater;
import de.tud.feedback.plugin.OpenHabMonitorAgent;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class DogOntContextUpdaterFactoryBean extends AbstractFactoryBean<DogOntContextUpdater> {

    private CypherExecutor executor;

    private Long context;

    public static DogOntContextUpdaterFactoryBean build() {
        return new DogOntContextUpdaterFactoryBean();
    }

    public void setExecutor(CypherExecutor executor) {
        this.executor = executor;
    }

    public void setContext(Long context) {
        this.context = context;
    }

    @Override
    public Class<?> getObjectType() {
        return OpenHabMonitorAgent.class;
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
