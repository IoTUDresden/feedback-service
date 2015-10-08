package de.tud.feedback.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

@Configuration
public class RepositoryConfiguration extends RepositoryRestConfigurerAdapter {

    @Autowired JsonSerializer<Resource> resourceSerializer;

    @Autowired JsonDeserializer<Resource> resourceDeserializer;

    @Override
    public void configureJacksonObjectMapper(ObjectMapper mapper) {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new SimpleModule()
                .addDeserializer(Resource.class, resourceDeserializer)
                .addSerializer(Resource.class, resourceSerializer));
    }

}
