package de.tud.feedback.plugin.factory;

import de.tud.feedback.CypherExecutor;
import de.tud.feedback.plugin.HealingCompensationRepository;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Stefan on 14.06.2016.
 */
public class HealingCompensationRepositoryFactoryBean extends AbstractFactoryBean<HealingCompensationRepository> {
    private static final String QUERY_FILE_PATH = "classpath:compensation-peer-query.cypher";

    private CypherExecutor executor;

    private ResourceLoader loader;

    @Override
    public Class<?> getObjectType() {
        return HealingCompensationRepository.class;
    }

    @Override
    protected HealingCompensationRepository createInstance() throws Exception {
        checkNotNull(executor, "CypherExecutor is missing");
        checkNotNull(loader, "ResourceLoader is missing");
        return new HealingCompensationRepository(executor,queryFromResource());
    }
    private String queryFromResource() throws IOException {
        InputStream inputStream = loader.getResource(QUERY_FILE_PATH).getInputStream();
        return StreamUtils.copyToString(inputStream, Charset.defaultCharset());
    }
    @Override
    public boolean isSingleton() {
        return false;
    }

    public HealingCompensationRepositoryFactoryBean setExecutor(CypherExecutor executor) {
        this.executor = executor;
        return this;
    }

    public HealingCompensationRepositoryFactoryBean setLoader(ResourceLoader loader) {
        this.loader = loader;
        return this;
    }
}
