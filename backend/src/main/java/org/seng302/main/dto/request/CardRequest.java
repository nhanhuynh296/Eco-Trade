package org.seng302.main.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class CardRequest {

    private Long creatorId;
    private String section;
    private String title;
    private String description;
    private ArrayList<String> keywords;

    /**
     * Card DTO Request
     * @param creatorId - userID
     * @param section - card section
     * @param description - card description
     * @param keywords - list of card keyword
     */
    public CardRequest(Long creatorId, String section, String title, String description, ArrayList<String> keywords) {
        this.creatorId = creatorId;
        this.section = section;
        this.title = title;
        this.description = description;
        this.keywords = keywords;
    }

    /**
     * Empty constructor for automapping requests
     */
    public CardRequest(){}
}
