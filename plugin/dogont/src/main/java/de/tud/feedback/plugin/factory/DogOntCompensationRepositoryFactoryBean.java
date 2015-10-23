package de.tud.feedback.plugin.factory;

import de.tud.feedback.CypherExecutor;
import de.tud.feedback.plugin.DogOntCommandRepository;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class DogOntCompensationRepositoryFactoryBean extends AbstractFactoryBean<DogOntCommandRepository> {

    private CypherExecutor executor;
    
    @Override
    protected DogOntCommandRepository createInstance() throws Exception {
        checkNotNull(executor, "CypherExecutor is missing");

        return new DogOntCommandRepository(executor);
    }

    public DogOntCompensationRepositoryFactoryBean setExecutor(CypherExecutor executor) {
        this.executor = checkNotNull(executor);
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return DogOntCommandRepository.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
