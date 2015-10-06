package de.tud.feedback.plugin.openhab;

import de.tud.feedback.plugin.openhab.domain.OpenHabItemContainer;
import feign.Headers;
import feign.RequestLine;

@Headers("Accept: application/json")
public interface OpenHabService {

    @RequestLine("GET /rest/items/")
    OpenHabItemContainer getAllItems();

}
