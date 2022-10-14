package org.seng302.main.responseFactoryTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.response.KeywordResponse;
import org.seng302.main.dto.responsefactory.KeywordResponses;
import org.seng302.main.models.Keyword;
import org.seng302.main.repository.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Test keyword responses
 */
@SpringBootTest
class KeywordResponsesTests {

    @Autowired
    KeywordRepository keywordRepository;

    Keyword testKeyword;

    KeywordResponse testKeywordResponse;

    /**
     * Initialising all the variables before each test
     */
    @BeforeEach
    public void init() {
        testKeyword = new Keyword("keyword", LocalDate.now());
        testKeyword = keywordRepository.save(testKeyword);

        testKeywordResponse = new KeywordResponse();
        testKeywordResponse.setId(testKeyword.getId());
        testKeywordResponse.setName("keyword");
        testKeywordResponse.setCreated(LocalDate.now());
    }

    /**
     * Testing for getting keyword response
     */
    @Test
    void testKeywordResponse() {
        KeywordResponse response = KeywordResponses.getResponse(testKeyword);

        Assertions.assertEquals(testKeywordResponse.getId(), response.getId());
        Assertions.assertEquals(testKeywordResponse.getName(), response.getName());
        Assertions.assertEquals(testKeywordResponse.getCreated(), response.getCreated());
    }

    /**
     * Testing for getting keyword responses
     */
    @Test
    void testKeywordResponses() {
        List<KeywordResponse> check = new ArrayList<>();
        check.add(testKeywordResponse);

        List<Keyword> keywords = new ArrayList<>();
        keywords.add(testKeyword);

        List<KeywordResponse> responses = KeywordResponses.getAllResponses(keywords);

        Assertions.assertEquals(check.size(), responses.size());
        Assertions.assertEquals(check.get(0).getId(), responses.get(0).getId());
    }

}
