package de.tud.feedback.plugin.openhab.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class OpenHabItemContainer {

    @JsonProperty("item")
    private Collection<OpenHabItem> items;

    public Collection<OpenHabItem> getItems() {
        return items;
    }

    public void setItems(Collection<OpenHabItem> items) {
        this.items = items;
    }

}
