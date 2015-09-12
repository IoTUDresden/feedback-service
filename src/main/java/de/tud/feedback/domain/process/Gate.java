package de.tud.feedback.domain.process;

import de.tud.feedback.knowledge.OptionalLongAttributeConverter;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.util.Optional;

@NodeEntity
public class Gate {

    @GraphId
    private Long id;

    @Convert(OptionalLongAttributeConverter.class)
    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

}
