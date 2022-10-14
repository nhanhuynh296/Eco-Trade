package org.seng302.main.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.seng302.main.models.Keyword;

import java.time.LocalDate;

/**
 * Keyword response DTO object
 */
@Getter
@Setter
public class KeywordResponse {

    private long id;
    private String name;
    private LocalDate created;

}
