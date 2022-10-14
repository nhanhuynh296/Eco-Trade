package org.seng302.main.dto.responsefactory;

import org.seng302.main.dto.response.KeywordResponse;
import org.seng302.main.models.Keyword;

import java.util.ArrayList;
import java.util.List;

/**
 * Keyword Response factory builder class
 */
public class KeywordResponses {

    /**
     * Add explicit private constructor
     */
    private KeywordResponses() {}

    /**
     * Creates a single response for keyword
     *
     * @param keyword instance of Keyword
     * @return keyword response
     */
    public static KeywordResponse getResponse(Keyword keyword) {
        KeywordResponse keywordResponse = new KeywordResponse();

        keywordResponse.setId(keyword.getId());
        keywordResponse.setName(keyword.getName());
        keywordResponse.setCreated(keyword.getCreated());

        return keywordResponse;
    }

    /**
     * Creates a list of keyword responses
     *
     * @param keywords List of Keyword instances
     * @return List of keyword responses
     */
    public static List<KeywordResponse> getAllResponses(List<Keyword> keywords) {
        ArrayList<KeywordResponse> keywordResponses = new ArrayList<>();

        for (Keyword keyword: keywords) {
            keywordResponses.add(getResponse(keyword));
        }

        return keywordResponses;
    }

}
