package de.tud.feedback.plugin.factory;

import de.tud.feedback.CypherExecutor;
import de.tud.feedback.plugin.DogOntCompensationRepository;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class DogOntCompensationRepositoryFactoryBean extends AbstractFactoryBean<DogOntCompensationRepository> {

    private CypherExecutor executor;
    
    @Override
    protected DogOntCompensationRepository createInstance() throws Exception {
        checkNotNull(executor, "CypherExecutor is missing");

        return new DogOntCompensationRepository(executor);
    }

    public DogOntCompensationRepositoryFactoryBean setExecutor(CypherExecutor executor) {
        this.executor = checkNotNull(executor);
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return DogOntCompensationRepository.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
