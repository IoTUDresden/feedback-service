package de.tud.feedback.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

@NodeEntity
public class ContextImport extends Node {

    @URL
    @NotBlank
    private String source;

    @NotBlank
    private String mime;

    @NotBlank
    private String name;

    @JsonIgnore
    @Relationship(type = "createdBy", direction = Relationship.INCOMING)
    private Set<Node> entranceNodes = newHashSet();

    @JsonIgnore
    @Relationship(type = "importedFor", direction = Relationship.OUTGOING)
    private Context context;

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

    public Set<Node> getEntranceNodes() {
        return entranceNodes;
    }

    public void setEntranceNodes(Set<Node> entranceNodes) {
        this.entranceNodes = entranceNodes;
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
