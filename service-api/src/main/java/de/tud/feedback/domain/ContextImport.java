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

import static com.google.common.collect.Sets.newHashSet;

@NodeEntity
public class ContextImport {

    @GraphId
    private Long id;

    @NotNull
    @Convert(graphPropertyType = String.class)
    private Resource source;

    @NotBlank
    private String mime;

    @NotBlank
    private String name;

    @JsonIgnore
    @Relationship(type = "within", direction = Relationship.INCOMING)
    private Set<ContextNode> contextNodes = newHashSet();

    @JsonIgnore
    @Relationship(type = "for", direction = Relationship.OUTGOING)
    private Context context;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public Resource getSource() {
        return source;
    }

    public void setSource(Resource source) {
        this.source = source;
    }

    public Set<ContextNode> getContextNodes() {
        return contextNodes;
    }

    public void setContextNodes(Set<ContextNode> contextNodes) {
        this.contextNodes = contextNodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
