package de.tud.feedback.plugin.openhab;

import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.Collection;

@Headers("Accept: application/json")
public interface OpenHabService {

    @RequestLine("GET /rest/items/")
    Collection<OpenHabItem> getAllItems();

    @Body("{command}")
    @Headers("Content-Type: text/plain")
    @RequestLine("POST /rest/items/{item}")
    void executeCommand(@Param("item") String item, @Param("command") String command);

}
