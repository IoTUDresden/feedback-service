package de.tud.feedback.plugin.factory;

import de.tud.feedback.plugin.openhab.OpenHabService;
import feign.Feign;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class OpenHabServiceFactoryBean extends AbstractFactoryBean<OpenHabService> {

    private static final int RETRY_PERIOD = 5000;

    private static final int RETRY_MAX_PERIOD = 30000;

    private static final int RETRY_MAX_ATTEMPTS = 10;

    private String host;

    private Integer port;

    @Override
    protected OpenHabService createInstance() throws Exception {
        checkNotNull(host, "Host is missing");
        checkNotNull(port, "Port is missing");

        return Feign.builder()
                .decoder(new JacksonDecoder())
                .retryer(new Retryer.Default(RETRY_PERIOD, RETRY_MAX_PERIOD, RETRY_MAX_ATTEMPTS))
                .target(OpenHabService.class, format("http://%s:%s", host, port));
    }


    public OpenHabServiceFactoryBean setHost(String host) {
        this.host = checkNotNull(host);
        return this;
    }

    public OpenHabServiceFactoryBean setPort(Integer port) {
        this.port = checkNotNull(port);
        return this;
    }


    @Override
    public Class<?> getObjectType() {
        return OpenHabService.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
