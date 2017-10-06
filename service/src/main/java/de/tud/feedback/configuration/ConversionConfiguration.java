package de.tud.feedback.configuration;

import org.joda.time.DateTime;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.neo4j.conversion.MetaDataDrivenConversionService;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.util.Collection;

@Configuration
public class ConversionConfiguration {

    @Bean
    public ConversionService conversionService(
            SessionFactory neo4jSessionFactory,
            @Qualifier("customConverter") Collection<Converter> converters
    ) {
        MetaDataDrivenConversionService conversionService
                = new MetaDataDrivenConversionService(neo4jSessionFactory.metaData());

        converters.forEach(conversionService::addConverter);
        return conversionService;
    }

    @Qualifier("customConverter")
    @Bean Converter<String, Resource> stringResourceConverter(ResourceLoader loader) {
        //noinspection Convert2Lambda,Anonymous2MethodRef
        return new Converter<String, Resource>() {
            public Resource convert(String source) {
                return loader.getResource(source);
            }
        };
    }

    @Qualifier("customConverter")
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

    @Qualifier("customConverter")
    @Bean Converter<String, MimeType> stringMimeTypeConverter() {
        //noinspection Convert2Lambda, Anonymous2MethodRef
        return new Converter<String, MimeType>() {
            public MimeType convert(String source) {
                return MimeType.valueOf(source);
            }
        };
    }

    @Qualifier("customConverter")
    @Bean Converter<MimeType, String> mimeTypeStringConverter() {
        //noinspection Convert2Lambda, Anonymous2MethodRef
        return new Converter<MimeType, String>() {
            public String convert(MimeType source) {
                return source.toString();
            }
        };
    }

    @Qualifier("customConverter")
    @Bean Converter<String, DateTime> stringDateTimeConverter() {
        //noinspection Convert2Lambda, Anonymous2MethodRef
        return new Converter<String, DateTime>() {
            public DateTime convert(String source) {
                return DateTime.parse(source);
            }
        };
    }

    @Qualifier("customConverter")
    @Bean Converter<DateTime, String> DateTimeStringConverter() {
        //noinspection Convert2Lambda, Anonymous2MethodRef
        return new Converter<DateTime, String>() {
            public String convert(DateTime source) {
                return source.toString();
            }
        };
    }
    
}
