package org.seng302.main.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Card Data Transfer Object
 */
@Getter
@Setter
@NoArgsConstructor
public class CardResponse {

    private Long id;
    private UserResponse creator;
    private String section;
    private LocalDateTime created;
    private java.time.LocalDateTime displayPeriodEnd;
    private String title;
    private String description;
    private List<KeywordResponse> keywords;

}
