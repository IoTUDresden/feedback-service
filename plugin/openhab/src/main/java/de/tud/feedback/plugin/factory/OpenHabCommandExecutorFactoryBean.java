package de.tud.feedback.plugin.factory;

import de.tud.feedback.plugin.OpenHabCommandExecutor;
import de.tud.feedback.plugin.openhab.OpenHabService;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class OpenHabCommandExecutorFactoryBean extends AbstractFactoryBean<OpenHabCommandExecutor> {

    private OpenHabService service;

    private Function<String, String> itemNameMapper = s -> s;

    @Override
    protected OpenHabCommandExecutor createInstance() throws Exception {
        checkNotNull(service, "OpenHabService is missing");

        return new OpenHabCommandExecutor(service, itemNameMapper);
    }

    public OpenHabCommandExecutorFactoryBean setService(OpenHabService service) {
        this.service = checkNotNull(service);
        return this;
    }

    public OpenHabCommandExecutorFactoryBean setItemNameMapper(Function<String, String> itemNameMapper) {
        this.itemNameMapper = checkNotNull(itemNameMapper);
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return OpenHabCommandExecutor.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
