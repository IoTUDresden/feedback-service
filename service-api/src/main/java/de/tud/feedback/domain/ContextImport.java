package de.tud.feedback.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeType;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;

@NodeEntity
@SuppressWarnings("unused")
public class ContextImport {

    @GraphId
    private Long id;

    @NotNull
    @Property
    @JsonIgnore
    @Convert(graphPropertyType = String.class)
    private Resource source;

    @NotNull
    @Property
    @Convert(graphPropertyType = String.class)
    private MimeType mimeType;

    @NotNull
    @Property
    private String name;

    @JsonIgnore
    @Relationship(type = "within", direction = Relationship.INCOMING)
    @JsonBackReference("import-node")
    private Set<ContextNode> contextNodes = newHashSet();

    @Relationship(type = "for", direction = Relationship.OUTGOING)
    @JsonManagedReference("context-import")
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

    public Collection<ContextNode> getContextNodes() {
        return contextNodes;
    }

    public void setContextNodes(Set<ContextNode> contextNodes) {
        try {
            this.contextNodes = checkNotNull(contextNodes);
        } catch (NullPointerException exception) {
            this.contextNodes = newHashSet();
        }
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
