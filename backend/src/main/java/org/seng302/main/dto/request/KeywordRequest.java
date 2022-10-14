package org.seng302.main.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeywordRequest {
    private String name;

    /**
     * Keyword request constructor DTO
     * @param name Name of keyword
     */
    public KeywordRequest(String name) {
        this.name = name;
    }

    /**
     * Empty constructor for automapping requests
     */
    public KeywordRequest() {}
}
