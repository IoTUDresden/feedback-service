package de.tud.feedback.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.core.io.Resource;

import javax.validation.constraints.NotNull;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

@NodeEntity
public class Workflow {

    @GraphId
    private Long id;

    @Relationship(type = "instanceOf", direction = Relationship.INCOMING)
    private Set<WorkflowInstance> instances = newHashSet();

    @NotBlank
    private String name;

    @Relationship(type = "runsWithin", direction = Relationship.OUTGOING)
    private Context context;

    @NotNull
    @Convert(graphPropertyType = String.class)
    private Resource source;

    @NotBlank
    private String mime;

    @JsonIgnore
    private Resource resource;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<WorkflowInstance> getInstances() {
        return instances;
    }

    public void setInstances(Set<WorkflowInstance> instances) {
        try {
            this.instances = checkNotNull(instances);
        } catch (NullPointerException exception) {
            this.instances = newHashSet();
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Resource getSource() {
        return source;
    }

    public void setSource(Resource source) {
        this.source = source;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

}
