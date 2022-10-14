package org.seng302.main.serviceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.request.KeywordRequest;
import org.seng302.main.dto.response.KeywordResponse;
import org.seng302.main.models.Address;
import org.seng302.main.models.Keyword;
import org.seng302.main.models.User;
import org.seng302.main.repository.CardRepository;
import org.seng302.main.repository.KeywordRepository;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class KeywordServiceTests {

    @Autowired
    KeywordService keywordService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    KeywordRepository keywordRepository;

    @Autowired
    CardRepository cardRepository;

    private final String[] keywords = new String[] {
            "Truck", "Fruit", "Lemon", "Animal",
            "Dog", "Cat", "Truck2", "Towel"
    };

    private final User creator = new User("Juice", "", "Boy", "JB", "", "jb@juice.com",
            LocalDate.now(), "", new Address("", "", "", "", "", ""), LocalDate.now(), "ROLE_USER",
            "password123", "123", null);

    private Long keywordId;

    private final String longName = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private final String validName = "Muffins";

    @BeforeEach
    public void init() {
        userRepository.save(creator);
        cardRepository.deleteAll();
        keywordRepository.deleteAll();
        for (String keyword : keywords) {
            keywordRepository.save(new Keyword(keyword, LocalDate.now()));
        }
    }

    /**
     * Tests invalid keyword data details for null name
     */
    @Test
    void testInvalidNullTitle_expectThrows() {
        KeywordRequest request = new KeywordRequest(null);
        Assertions.assertThrows(ResponseStatusException.class, () -> keywordService.validateKeywordRequest(request));
    }

    /**
     * Tests invalid keyword data details for long name
     */
    @Test
    void testInvalidLongName_expectThrows() {
        KeywordRequest request = new KeywordRequest(longName);
        Assertions.assertThrows(ResponseStatusException.class, () -> keywordService.validateKeywordRequest(request));
    }

    /**
     * Tests valid keyword data details for valid name
     */
    @Test
    void testValidName_expectNotThrows() {
        KeywordRequest request = new KeywordRequest(validName);
        keywordService.validateKeywordRequest(request);
    }

    /**
     * Tests create keyword from valid data
     */
    @Test
    void testValidCreate_expectSuccess() {
        KeywordRequest request = new KeywordRequest(validName);
        keywordId = keywordService.createKeyword(creator, request).getId();
        Assertions.assertTrue(keywordRepository.existsById(keywordId));
    }

    /**
     * Test getting all keywords (No query)
     */
    @Test
    void testQueryAllKeywords() {
        List<KeywordResponse> keywordList = keywordService.getAllKeywordsLike("");
        Assertions.assertEquals(keywords.length, keywordList.size());
    }

    /**
     * Test getting lemon
     */
    @Test
    void testQueryOneKeyword() {
        List<KeywordResponse> keywordList = keywordService.getAllKeywordsLike("Lemon");
        Assertions.assertEquals(1, keywordList.size());
        Assertions.assertEquals("Lemon", keywordList.get(0).getName());
    }

    /**
     * Test getting keywords with like predicate
     */
    @Test
    void testQuerySomeKeywords() {
        List<KeywordResponse> keywordList = keywordService.getAllKeywordsLike("t");
        Assertions.assertEquals(5, keywordList.size());

        boolean found = false;
        for (KeywordResponse response : keywordList) {
            if (response.getName().equals("Towel")) {
                found = true;
                break;
            }
        }

        Assertions.assertTrue(found);
    }
}
