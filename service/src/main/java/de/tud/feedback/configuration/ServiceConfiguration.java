package de.tud.feedback.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;

@EnableAsync
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ServiceConfiguration {

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

}
