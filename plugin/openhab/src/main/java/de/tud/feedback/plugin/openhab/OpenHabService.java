package de.tud.feedback.plugin.openhab;

import feign.Headers;
import feign.RequestLine;

import java.util.Collection;

@Headers("Accept: application/json")
public interface OpenHabService {

    @RequestLine("GET /rest/items/")
    Collection<OpenHabItem> getAllItems();

}
