package de.tud.feedback.knowledge;

import org.neo4j.ogm.typeconversion.AttributeConverter;

import java.io.Serializable;
import java.util.Optional;

public abstract class OptionalAttributeConverter<T extends Serializable> implements AttributeConverter<Optional<T>, T> {

    @Override
    public T toGraphProperty(Optional<T> value) {
        return value.get();
    }

    @Override
    public Optional<T> toEntityAttribute(T value) {
        return Optional.ofNullable(value);
    }

}
