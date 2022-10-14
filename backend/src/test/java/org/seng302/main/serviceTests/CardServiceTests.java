package org.seng302.main.serviceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.request.CardRequest;
import org.seng302.main.models.Address;
import org.seng302.main.models.Card;
import org.seng302.main.models.Keyword;
import org.seng302.main.models.User;
import org.seng302.main.repository.KeywordRepository;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the CardService class
 */
@SpringBootTest
class CardServiceTests {

    @Autowired
    private CardService cardService;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private UserRepository userRepository;

    private CardRequest cardRequest;
    private CardRequest cardUpdate;
    private Card card;
    private Card updatedCard;
    private final String validTitle = "Food";
    private final String longTitle = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private final String validDescription = "Delicious";
    private final String longDescription =
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                    +
                    "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private final String section = "ForSale";
    private final List<Keyword> keywords = new ArrayList<>();
    private User validUser = new User();
    private User invalidUser = new User();

    /**
     * Before each test keyword, address, users, card will be created and a JSONObject card will be
     * created
     */
    @BeforeEach
    public void init() {
        Address address = new Address("1", "Nowhere", "Varrock", "Tirannwn", "Gilenor", "800");
        Address address2 = new Address("12", "Nowhere2", "Varrock2", "Tirannwn2", "Gilenor2",
                "8002");

        Keyword keyword = new Keyword("Car", LocalDate.now());
        keyword = keywordRepository.save(keyword);
        keywords.add(keyword);

        validUser.setUserRole("ROLE_DEFAULT_ADMIN");
        validUser.setDateOfBirth(LocalDate.of(2000, 12, 11));
        validUser.setEmail("email@email.com");
        validUser.setFirstName("Sasha");
        validUser.setLastName("Thomas");
        validUser.setHomeAddress(address);
        validUser.setPassword("password");
        validUser = userRepository.save(validUser);

        invalidUser.setUserRole("ROLE_USER");
        invalidUser.setDateOfBirth(LocalDate.of(2000, 12, 11));
        invalidUser.setEmail("email2@email.com");
        invalidUser.setFirstName("Sasha");
        invalidUser.setLastName("Thomas");
        invalidUser.setHomeAddress(address2);
        invalidUser.setPassword("password2");
        invalidUser = userRepository.save(invalidUser);

        ArrayList<String> keywordArray = new ArrayList<>();
//        keywordArray.add(keyword.getId());
        cardRequest = new CardRequest(validUser.getId(), null, null, null, keywordArray);
        cardRequest.setSection(section);
        cardRequest.setTitle(validTitle);
        cardRequest.setDescription(validDescription);

        cardUpdate = new CardRequest();

        card = new Card();
        card.setCreator(validUser);
        card.setSection(section);
        card.setCreated(LocalDateTime.now());
        card.setDisplayPeriodEnd(LocalDateTime.now().plusWeeks(2));
        card.setDescription(validDescription);
        card.setTitle(validTitle);
        card.setKeywords(keywords);
    }

    /**
     * Tests if current user who is not a GAA and is not the card user
     */
    @Test
    void testInvalidUser() {
        Assertions.assertFalse(cardService.isGAAorMainUser(validUser, invalidUser));
    }

    /**
     * Tests valid user if the current user and the card user are the same
     */
    @Test
    void testValidUser() {
        Assertions.assertTrue(cardService.isGAAorMainUser(validUser, validUser));
    }

    /**
     * Tests valid user in the current user is a GAA
     */
    @Test
    void testValidUserDGAA() {
        Assertions.assertTrue(cardService.isGAAorMainUser(invalidUser, validUser));
    }

    /**
     * Tests invalid card details for null title
     */
    @Test
    void testInvalidNullTitleCardDetails() {
        card.setTitle(null);
        Assertions.assertNotEquals("", cardService.isValidCard(card));
    }

    /**
     * Tests invalid card details for empty title
     */
    @Test
    void testInvalidEmptyTitleCardDetails() {
        card.setTitle("");
        Assertions.assertNotEquals("", cardService.isValidCard(card));
    }

    /**
     * Tests invalid card details for long title
     */
    @Test
    void testInvalidLongTitleCardDetails() {
        card.setTitle(longTitle);
        Assertions.assertNotEquals("", cardService.isValidCard(card));
    }

    /**
     * Tests invalid card details for null selection
     */
    @Test
    void testInvalidNullSelectionCardDetails() {
        card.setSection(null);
        Assertions.assertNotEquals("", cardService.isValidCard(card));
    }

    /**
     * Tests invalid card details for long description
     */
    @Test
    void testInvalidLongDescriptionCardDetails() {
        card.setDescription(longDescription);
        Assertions.assertNotEquals("", cardService.isValidCard(card));
    }

    /**
     * Tests valid card details
     */
    @Test
    void testValidCardDetails() {
        Assertions.assertEquals("", cardService.isValidCard(card));
    }

    /**
     * Tests creating a card with invalid card details
     */
    @Test
    void testInvalidDetailsCreateCard() {
        cardRequest.setSection(null);
        cardRequest.setTitle(longTitle);
        cardRequest.setDescription(longDescription);

        Assertions.assertThrows(ResponseStatusException.class, () -> cardService.createCard(validUser, cardRequest));
    }

    /**
     * Tests creating a card with invalid user
     */
    @Test
    void testInvalidUserCreateCard() {
        cardRequest.setSection(section);
        cardRequest.setTitle(validTitle);
        cardRequest.setDescription(validDescription);

        Assertions.assertThrows(ResponseStatusException.class, () -> cardService.createCard(invalidUser, cardRequest));
    }

    /**
     * Tests creating a valid card
     */
    @Test
    void testCreateValidCreateCard() {
        cardRequest.setSection(section);
        cardRequest.setTitle(validTitle);
        cardRequest.setDescription(validDescription);

        Assertions.assertDoesNotThrow(() -> {
            Card card = cardService.createCard(validUser, cardRequest);
            Assertions.assertNotNull(card.getId());
        });
    }

    /**
     * Test updating cards title
     */
    @Test
    void testUpdateCardTitle() {
        card = cardService.createCard(validUser, cardRequest);

        cardUpdate.setTitle("New valid title");
        updatedCard = cardService.updateCard(validUser, cardUpdate, card.getId());

        Assertions.assertNotEquals(card.getTitle(), "New valid title");
        Assertions.assertEquals(updatedCard.getTitle(), "New valid title");
    }

    /**
     * Test updating cards title to invalid title
     */
    @Test
    void testUpdateCardInvalidTitle() {
        card = cardService.createCard(validUser, cardRequest);

        cardUpdate.setTitle(longTitle);
        Assertions.assertThrows(ResponseStatusException.class, () -> cardService.updateCard(validUser, cardUpdate, card.getId()));
    }

    /**
     * Test updating cards description
     */
    @Test
    void testUpdateCardDescription() {
        card = cardService.createCard(validUser, cardRequest);

        cardUpdate.setDescription("New valid description");
        updatedCard = cardService.updateCard(validUser, cardUpdate, card.getId());

        Assertions.assertNotEquals("New valid description", card.getDescription());
        Assertions.assertEquals("New valid description", updatedCard.getDescription());
    }

    /**
     * Test updating cards keywords
     */
    @Test
    @Transactional
    void testUpdateCardKeywords() {
        card = cardService.createCard(validUser, cardRequest);
        List<Keyword> oldKeywords = card.getKeywords();


        //Update the keywords of the card
        ArrayList<String> cardKeywords =new ArrayList<>();
            cardKeywords.add("One");
            cardKeywords.add("Two");
        cardUpdate.setKeywords(cardKeywords);
        Card updatedCard =  cardService.updateCard(validUser, cardUpdate, card.getId());
        //Ensure the new keywords are saved to the existing card
        List<Keyword> newKeywords = updatedCard.getKeywords();

        Keyword one = keywordRepository.findByName("One");
        Keyword two = keywordRepository.findByName("Two");
        Assertions.assertNotEquals(oldKeywords, newKeywords);
        Assertions.assertFalse(oldKeywords.toString().contains(one.toString()));
        Assertions.assertFalse(oldKeywords.toString().contains(two.toString()));
        Assertions.assertTrue(newKeywords.toString().contains(one.toString()));
        Assertions.assertTrue(newKeywords.toString().contains(two.toString()));
    }

    /**
     * Test updating cards description to invalid description
     */
    @Test
    void testUpdateCardInvalidDescription() {
        card = cardService.createCard(validUser, cardRequest);

        cardUpdate.setTitle(longDescription);
        Assertions.assertThrows(ResponseStatusException.class, () -> cardService.updateCard(validUser, cardUpdate, card.getId()));
    }

    /**
     * Test updating cards section doesn't change section
     */
    @Test
    void testUpdateCardNewSectionInvalid() {
        card = cardService.createCard(validUser, cardRequest);

        cardUpdate.setSection("Invalid Section");
        updatedCard = cardService.updateCard(validUser, cardUpdate, card.getId());

        Assertions.assertNotEquals("Invalid Section", updatedCard.getSection());
        Assertions.assertEquals(section, card.getSection());
    }

    /**
     * Test updating card, creator remain unchanged
     */
    @Test
    void testUpdateCardCreatorRemainsUnchanged() {
        card = cardService.createCard(validUser, cardRequest);
        Assertions.assertEquals(card.getCreator().getId(), validUser.getId());

        cardService.updateCard(validUser, cardUpdate, card.getId());
        Assertions.assertEquals(card.getCreator().getId(), validUser.getId());
    }

    /**
     * Test updating card, created date remain unchanged
     */
    @Test
    void testUpdateCardCreatedDateRemainsUnchanged() {
        card = cardService.createCard(validUser, cardRequest);
        LocalDateTime created = card.getCreated();

        cardService.updateCard(validUser, cardUpdate, card.getId());
        Assertions.assertEquals(card.getCreated(), created);
    }

}
