package de.tud.feedback.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

@NodeEntity
public class ContextImport {

    @GraphId
    private Long id;

    @URL
    @NotBlank
    private String source;

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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
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
