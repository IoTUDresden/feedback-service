package de.tud.feedback.plugin.factory;

import de.tud.feedback.CypherExecutor;
import de.tud.feedback.plugin.DogOntCompensationRepository;
import de.tud.feedback.repository.CompensationRepositoryFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkNotNull;

public class DogOntCompensationRepositoryFactoryBean extends AbstractFactoryBean<DogOntCompensationRepository>
        implements CompensationRepositoryFactory<DogOntCompensationRepositoryFactoryBean> {

    private static final String QUERY_FILE_PATH = "classpath:compensation-query.cypher";

    private CypherExecutor executor;

    private ResourceLoader loader;

    @Override
    protected DogOntCompensationRepository createInstance() throws Exception {
        checkNotNull(executor, "CypherExecutor is missing");
        checkNotNull(loader, "ResourceLoader is missing");

        return new DogOntCompensationRepository(executor, queryFromResource());
    }

    public DogOntCompensationRepositoryFactoryBean setExecutor(CypherExecutor executor) {
        this.executor = checkNotNull(executor);
        return this;
    }

    @Override
    public DogOntCompensationRepositoryFactoryBean setLoader(ResourceLoader loader) {
        this.loader = checkNotNull(loader);
        return this;
    }

    private String queryFromResource() throws IOException {
        InputStream inputStream = loader.getResource(QUERY_FILE_PATH).getInputStream();
        return StreamUtils.copyToString(inputStream, Charset.defaultCharset());
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
