package de.tud.feedback.plugin.factory;


import de.tud.feedback.CypherExecutor;
import de.tud.feedback.plugin.ProteusCompensationRepository;
import de.tud.feedback.repository.CompensationRepositoryFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkNotNull;

public class ProteusCompensationRepositoryFactoryBean extends AbstractFactoryBean<ProteusCompensationRepository>
    implements CompensationRepositoryFactory<ProteusCompensationRepositoryFactoryBean>{

    private static final String QUERY_FILE_PATH = "classpath:proteus-compensation-query.cypher";

    private CypherExecutor executor;
    private ResourceLoader loader;

    @Override
    public Class<?> getObjectType() {
        return ProteusCompensationRepository.class;
    }

    @Override
    protected ProteusCompensationRepository createInstance() throws Exception {
        checkNotNull(executor, "CypherExecutor is missing");
        checkNotNull(loader, "ResourceLoader is missing");
        return new ProteusCompensationRepository(executor, queryFromResource());
    }

    public void setExecutor(CypherExecutor executor) {
        this.executor = executor;
    }

    @Override
    public ProteusCompensationRepositoryFactoryBean setLoader(ResourceLoader loader) {
        this.loader = checkNotNull(loader);
        return this;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    private String queryFromResource() throws IOException {
        InputStream inputStream = loader.getResource(QUERY_FILE_PATH).getInputStream();
        return StreamUtils.copyToString(inputStream, Charset.defaultCharset());
    }
}
