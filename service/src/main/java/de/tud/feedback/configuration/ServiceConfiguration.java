package de.tud.feedback.configuration;

import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.neo4j.conversion.MetaDataDrivenConversionService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.util.Arrays;

import static java.lang.String.format;

@EnableAsync
@EnableCaching
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ServiceConfiguration implements EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceConfiguration.class);

    @Bean
    public ConversionService conversionService(
            SessionFactory neo4jSessionFactory,
            Converter<String, Resource> stringResourceConverter,
            Converter<Resource, String> resourceStringConverter,
            Converter<MimeType, String> mimeTypeStringConverter,
            Converter<String, MimeType> stringMimeTypeConverter
    ) {
        GenericConversionService conversionService
                = new MetaDataDrivenConversionService(neo4jSessionFactory.metaData());

        conversionService.addConverter(stringResourceConverter);
        conversionService.addConverter(resourceStringConverter);
        conversionService.addConverter(stringMimeTypeConverter);
        conversionService.addConverter(mimeTypeStringConverter);

        return conversionService;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    @Bean Converter<String, Resource> stringResourceConverter(ResourceLoader loader) {
        //noinspection Convert2Lambda,Anonymous2MethodRef
        return new Converter<String, Resource>() {
            public Resource convert(String source) {
                return loader.getResource(source);
            }
        };
    }

    @Bean Converter<Resource, String> resourceStringConverter() {
        //noinspection Convert2Lambda
        return new Converter<Resource, String>() {
            public String convert(Resource source) {
                try {
                    return source.getURI().toString();
                } catch (IOException exception) {
                    return null;
                }
            }
        };
    }

    @Bean Converter<String, MimeType> stringMimeTypeConverter() {
        //noinspection Convert2Lambda, Anonymous2MethodRef
        return new Converter<String, MimeType>() {
            public MimeType convert(String source) {
                return MimeType.valueOf(source);
            }
        };
    }

    @Bean Converter<MimeType, String> mimeTypeStringConverter() {
        //noinspection Convert2Lambda, Anonymous2MethodRef
        return new Converter<MimeType, String>() {
            public String convert(MimeType source) {
                return source.toString();
            }
        };
    }

    @Override
    public void setEnvironment(Environment environment) {
        LOG.info(format("Active profiles: %s",
                Arrays.toString(environment.getActiveProfiles()).replaceAll("^\\[(.*)\\]$", "$1")));
    }

}
