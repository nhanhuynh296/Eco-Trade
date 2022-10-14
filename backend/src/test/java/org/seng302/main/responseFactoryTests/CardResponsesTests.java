package org.seng302.main.responseFactoryTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.response.CardResponse;
import org.seng302.main.dto.responsefactory.CardResponses;
import org.seng302.main.dto.responsefactory.KeywordResponses;
import org.seng302.main.dto.responsefactory.UserResponses;
import org.seng302.main.models.*;
import org.seng302.main.repository.CardRepository;
import org.seng302.main.repository.KeywordRepository;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Test card responses
 */
@SpringBootTest
class CardResponsesTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    KeywordRepository keywordRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    Address testAddress;
    User testUser;
    Keyword testKeyword;
    Card testCard;

    CardResponse testCardResponse;

    /**
     * Initialising all the variables before each test
     */
    @BeforeEach
    public void init() {
        testAddress = new Address("3/24", "Ilam Road", "Christchurch",
                "Canterbury", "New Zealand", "90210");

        testUser = new User("John", "", "Johnson", "Johnny", "empty Bio", "email@here.com.co",
                LocalDate.of(1970, 1, 1), "0200 9020", testAddress, LocalDate.now(), "ROLE_USER",
                passwordEncoder.encode("pass"), null, null);
        testUser = userRepository.save(testUser);

        testKeyword = new Keyword("Car", LocalDate.now());
        testKeyword = keywordRepository.save(testKeyword);
        List<Keyword> keywords = new ArrayList<>();
        keywords.add(testKeyword);

        testCard = new Card(testUser, "ForSale", LocalDateTime.now(), LocalDateTime.now().plusWeeks(2), "title", "card", keywords);
        testCard = cardRepository.save(testCard);

        testCardResponse = new CardResponse();
        testCardResponse.setId(testCard.getId());
        testCardResponse.setCreator(UserResponses.getResponse(testUser, false, false));
        testCardResponse.setSection("ForSale");
        testCardResponse.setCreated(LocalDateTime.now());
        testCardResponse.setDisplayPeriodEnd(LocalDateTime.now().plusWeeks(2));
        testCardResponse.setTitle("title");
        testCardResponse.setDescription("card");
        testCardResponse.setKeywords(KeywordResponses.getAllResponses(keywords));
    }

    /**
     * Testing for getting card response
     */
    @Test
    void testGettingCardResponse() {
        CardResponse response = CardResponses.getResponse(testCard);

        Assertions.assertEquals(testCardResponse.getId(), response.getId());
        Assertions.assertEquals(testCardResponse.getCreator().getId(), response.getCreator().getId());
        Assertions.assertEquals(testCardResponse.getSection(), response.getSection());
        Assertions.assertEquals(testCardResponse.getCreated().getDayOfMonth(), response.getCreated().getDayOfMonth());
        Assertions.assertEquals(testCardResponse.getDisplayPeriodEnd().getDayOfMonth(), response.getDisplayPeriodEnd().getDayOfMonth());
        Assertions.assertEquals(testCardResponse.getTitle(), response.getTitle());
        Assertions.assertEquals(testCardResponse.getDescription(), response.getDescription());
        Assertions.assertEquals(testCardResponse.getKeywords().get(0).getId(), response.getKeywords().get(0).getId());
    }

    /**
     * Testing for getting card responses
     */
    @Test
    void testGettingCardResponses() {
        List<CardResponse> check = new ArrayList<>();
        check.add(testCardResponse);

        List<Card> cards = new ArrayList<>();
        cards.add(testCard);

        List<CardResponse> responses = CardResponses.getAllResponses(cards);

        Assertions.assertEquals(check.size(), responses.size());
        Assertions.assertEquals(check.get(0).getId(), responses.get(0).getId());
    }

}
