package de.tud.feedback.domain;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

public class Context extends Node {

    @NotBlank
    private String name;

    @URL
    @NotBlank
    private String source;

    @NotBlank
    private String plugin;

    @Relationship(type = "partOf", direction = Relationship.INCOMING)
    private Set<Node> nodes = newHashSet();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Node> nodes) {
        try {
            this.nodes = checkNotNull(nodes);
        } catch (NullPointerException exception) {
            this.nodes = newHashSet();
        }
    }


}
