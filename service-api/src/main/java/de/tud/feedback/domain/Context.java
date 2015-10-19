package de.tud.feedback.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;
import org.neo4j.ogm.annotation.*;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

@NodeEntity
public class Context {

    @GraphId
    private Long id;

    @NotBlank
    @Property
    @Index(unique = true)
    private String name;

    @Relationship(type = "for", direction = Relationship.INCOMING)
    private List<ContextImport> imports = newArrayList();

    @JsonIgnore
    @Relationship(type = "runsWithin", direction = Relationship.INCOMING)
    private Collection<Workflow> workflows;

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

    public Collection<Workflow> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(Collection<Workflow> workflows) {
        try {
            this.workflows = checkNotNull(workflows);
        } catch (NullPointerException exception) {
            this.workflows = newArrayList();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Context context = (Context) o;

        return !(id != null ? !id.equals(context.id) : context.id != null) &&
               !(name != null ? !name.equals(context.name) : context.name != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return format("Context(%s)", name);
    }

}
