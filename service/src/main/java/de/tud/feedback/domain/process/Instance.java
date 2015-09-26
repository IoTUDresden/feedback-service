package de.tud.feedback.domain.process;

import de.tud.feedback.domain.Workflow;
import de.tud.feedback.knowledge.OptionalLongAttributeConverter;
import de.tud.feedback.knowledge.OptionalStringAttributeConverter;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@NodeEntity
public class Instance {

    @GraphId
    private Long id;

    @Relationship(type = "instanceOf", direction = Relationship.OUTGOING)
    private Workflow workflow;

    private String name;

    @Convert(OptionalLongAttributeConverter.class)
    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Convert(OptionalStringAttributeConverter.class)
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = checkNotNull(workflow);
    }

}
