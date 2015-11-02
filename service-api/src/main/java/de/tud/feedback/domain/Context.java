package de.tud.feedback.domain;

import org.neo4j.ogm.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;

@NodeEntity
@SuppressWarnings("unused")
public class Context {

    @GraphId
    private Long id;

    @NotNull
    @Property
    @Index(unique = true)
    private String name;

    @NotNull
    @Relationship(type = "for", direction = Relationship.INCOMING)
    private Set<ContextImport> imports = newHashSet();

    @Relationship(type = "runsWithin", direction = Relationship.INCOMING)
    private Set<Workflow> workflows;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<ContextImport> getImports() {
        return imports;
    }

    public void setImports(Set<ContextImport> imports) {
        try {
            this.imports = checkNotNull(imports);
        } catch (NullPointerException exception) {
            this.imports = newHashSet();
        }
    }

    public Collection<Workflow> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(Set<Workflow> workflows) {
        try {
            this.workflows = checkNotNull(workflows);
        } catch (NullPointerException exception) {
            this.workflows = newHashSet();
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
