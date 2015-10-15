package de.tud.feedback.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.util.MimeType;

import java.io.IOException;

@Configuration
public class RestRepositoryConfiguration extends RepositoryRestConfigurerAdapter {

    @Autowired JsonSerializer<Resource> resourceSerializer;

    @Autowired JsonDeserializer<Resource> resourceDeserializer;

    @Autowired JsonSerializer<MimeType> mimeTypeSerializer;

    @Autowired JsonDeserializer<MimeType> mimeTypeDeserializer;

    @Override
    public void configureJacksonObjectMapper(ObjectMapper mapper) {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JodaModule());
        mapper.registerModule(new SimpleModule()
                .addDeserializer(Resource.class, resourceDeserializer)
                .addSerializer(Resource.class, resourceSerializer)
                .addDeserializer(MimeType.class, mimeTypeDeserializer)
                .addSerializer(MimeType.class, mimeTypeSerializer));
    }

    @Bean
    JsonSerializer<Resource> resourceJsonSerializer(Converter<Resource, String> resourceStringConverter) {
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

    @Bean JsonSerializer<MimeType> mimeTypeJsonSerializer(Converter<MimeType, String> mimeTypeStringConverter) {
        return new JsonSerializer<MimeType>() {
            @Override
            public void serialize(MimeType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(mimeTypeStringConverter.convert(value));
            }

            @Override
            public Class<MimeType> handledType() {
                return MimeType.class;
            }
        };
    }

    @Bean JsonDeserializer<MimeType> mimeTypeJsonDeserializer(Converter<String, MimeType> stringMimeTypeConverter) {
        return new FromStringDeserializer<MimeType>(MimeType.class) {
            protected MimeType _deserialize(String value, DeserializationContext ctxt) throws IOException {
                return stringMimeTypeConverter.convert(value);
            }
        };
    }

}
