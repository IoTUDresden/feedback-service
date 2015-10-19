package de.tud.feedback.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeType;

import javax.validation.constraints.NotNull;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;

@NodeEntity
public class ContextImport {

    @GraphId
    private Long id;

    @NotNull
    @Property
    @Convert(graphPropertyType = String.class)
    private Resource source;

    @NotBlank
    @Property
    @Convert(graphPropertyType = String.class)
    private MimeType mimeType;

    @NotBlank
    @Property
    private String name;

    @JsonIgnore
    @Relationship(type = "within", direction = Relationship.INCOMING)
    private Set<ContextNode> contextNodes = newHashSet();

    @JsonIgnore
    @Relationship(type = "for", direction = Relationship.OUTGOING)
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
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
        return format("ContextImport(%s)", name);
    }

}
