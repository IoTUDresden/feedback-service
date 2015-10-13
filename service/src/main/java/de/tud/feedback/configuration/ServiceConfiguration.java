package de.tud.feedback.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import de.tud.feedback.WorkflowAugmentation;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
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
            Converter<Resource, String> resourceStringConverter
    ) {
        GenericConversionService conversionService
                = new MetaDataDrivenConversionService(neo4jSessionFactory.metaData());

        conversionService.addConverter(stringResourceConverter);
        conversionService.addConverter(resourceStringConverter);

        return conversionService;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            WorkflowAugmentation.CACHE
        );
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

    @Bean JsonSerializer<Resource> resourceJsonSerializer(Converter<Resource, String> resourceStringConverter) {
        return new JsonSerializer<Resource>() {
            @Override
            public void serialize(Resource value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(resourceStringConverter.convert(value));
            }

            @Override
            public Class<Resource> handledType() {
                return Resource.class;
            }
        };
    }

    @Bean JsonDeserializer<Resource> resourceJsonDeserializer(Converter<String, Resource> stringResourceConverter) {
        return new FromStringDeserializer<Resource>(Resource.class) {
            protected Resource _deserialize(String value, DeserializationContext ctxt) throws IOException {
                return stringResourceConverter.convert(value);
            }
        };
    }

    @Override
    public void setEnvironment(Environment environment) {
        LOG.info(format("Active profiles: %s",
                Arrays.toString(environment.getActiveProfiles()).replaceAll("^\\[(.*)\\]$", "$1")));
    }

}
