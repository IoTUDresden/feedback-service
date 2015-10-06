package de.tud.feedback.domain;

import de.tud.feedback.api.ContextReference;
import org.hibernate.validator.constraints.NotBlank;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

@NodeEntity
public class Context implements ContextReference {

    @GraphId
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String itemNamespace;

    @Relationship(type = "importedFor", direction = Relationship.INCOMING)
    private List<ContextImport> imports = newArrayList();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    public String getItemNamespace() {
        return itemNamespace;
    }

    public void setItemNamespace(String itemNamespace) {
        this.itemNamespace = itemNamespace;
    }

    @Override
    public String toString() {
        return format("%s, imports = %s", name, imports);
    }

}
