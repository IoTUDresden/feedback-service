package de.tud.feedback.domain;

import de.tud.feedback.domain.process.Instance;
import de.tud.feedback.knowledge.OptionalLongAttributeConverter;
import de.tud.feedback.knowledge.OptionalStringAttributeConverter;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

@NodeEntity
public class Workflow {

    @GraphId
    private Long id;

    @Relationship(type = "instanceOf", direction = Relationship.INCOMING)
    private Set<Instance> instances = newHashSet();

    private String name;

    @Convert(OptionalStringAttributeConverter.class)
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Convert(OptionalLongAttributeConverter.class)
    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Instance> getInstances() {
        return instances;
    }

    public void setInstances(Set<Instance> instances) {
        try {
            this.instances = checkNotNull(instances);
        } catch (NullPointerException exception) {
            this.instances = newHashSet();
        }
    }

}
