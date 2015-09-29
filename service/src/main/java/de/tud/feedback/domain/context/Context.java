package de.tud.feedback.domain.context;

import de.tud.feedback.domain.Node;
import org.hibernate.validator.constraints.NotBlank;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

@NodeEntity
public class Context extends Node {

    @NotBlank
    private String name;

    @NotBlank
    private String plugin;

    @Relationship(type = "importedFor", direction = Relationship.INCOMING)
    private Set<ContextImport> imports = newHashSet();

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

    public Set<ContextImport> getImports() {
        return imports;
    }

    public void setImports(Set<ContextImport> imports) {
        try {
            this.imports = checkNotNull(imports);
        } catch (NullPointerException exception) {
            this.imports = newHashSet();
        }
    }


}
