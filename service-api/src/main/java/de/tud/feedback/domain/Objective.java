package de.tud.feedback.domain;

import org.hibernate.validator.constraints.NotBlank;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.util.MimeType;

import java.util.Collection;

import static java.lang.String.format;
import static java.lang.String.join;

@NodeEntity
public class Objective {

    @GraphId
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String expression;

    @NotBlank
    @Convert(graphPropertyType = String.class)
    private MimeType mime;

    private boolean hasBeenMet = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isHasBeenMet() {
        return hasBeenMet;
    }

    public void setHasBeenMet(boolean hasBeenMet) {
        this.hasBeenMet = hasBeenMet;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setExpressions(Collection<String> lines) {
        this.expression = join("\n", lines);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MimeType getMime() {
        return mime;
    }

    public void setMime(MimeType mime) {
        this.mime = mime;
    }

    @Override
    public String toString() {
        return format("Objective(%s)", name);
    }

}
