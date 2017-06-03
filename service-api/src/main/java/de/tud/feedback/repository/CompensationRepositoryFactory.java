package de.tud.feedback.repository;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.ResourceLoader;

/**
 * Simple interface to indicate the compensation factories for dependency injection
 */
public interface CompensationRepositoryFactory<T extends AbstractFactoryBean<? extends CompensationRepository>> {

    T setLoader(ResourceLoader loader);
}
