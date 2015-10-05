package de.tud.feedback.domain;

import org.hibernate.validator.constraints.NotBlank;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

@NodeEntity
public class Context {

    @GraphId
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String plugin;

    @Relationship(type = "importedFor", direction = Relationship.INCOMING)
    private List<ContextImport> imports = newArrayList();

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

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public List<ContextImport> getImports() {
        return imports;
    }

    public void setImports(List<ContextImport> imports) {
        try {
            this.imports = checkNotNull(imports);
        } catch (NullPointerException exception) {
            this.imports = newArrayList();
        }
    }

    @Override
    public String toString() {
        return format("%s, imports = %s", name, imports);
    }

}
