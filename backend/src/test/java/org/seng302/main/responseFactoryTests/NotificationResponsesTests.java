package org.seng302.main.responseFactoryTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.response.NotificationResponse;
import org.seng302.main.dto.responsefactory.NotificationResponses;
import org.seng302.main.helpers.NotificationType;
import org.seng302.main.models.*;
import org.seng302.main.repository.*;
import org.seng302.main.services.BusinessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Test notification responses
 */
@SpringBootTest
class NotificationResponsesTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    KeywordRepository keywordRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    ListingRepository listingRepository;

    @Autowired
    NotificationRepository notificationRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    User testUser;
    Address testAddressOne;
    Address testAddressTwo;
    Keyword testKeyword;
    Card testCard;
    Product testProduct;
    InventoryItem testInventoryItem;
    Listing testListing;
    Business testBusiness;
    Notification testNotificationOne;
    Notification testNotificationTwo;
    Notification testNotificationThree;
    Notification testNotificationFour;

    NotificationResponse testNotificationResponseOne;
    NotificationResponse testNotificationResponseTwo;
    NotificationResponse testNotificationResponseThree;
    NotificationResponse testNotificationResponseFour;

    /**
     * Initialising all the variables before each test
     */
    @BeforeEach
    public void init() {
        testAddressOne = new Address("3/25", "Ilam Road", "Christchurch",
                "Canterbury", "New Zealand", "90210");
        testAddressTwo = new Address("3/24", "Ilam Road", "Christchurch",
                "Canterbury", "New Zealand", "90210");

        testUser = new User("John", "", "Johnson", "Johnny", "empty Bio", "email@here.com.co",
                LocalDate.of(1970, 1, 1), "0200 9020", testAddressOne, LocalDate.now(), "ROLE_USER",
                passwordEncoder.encode("pass"), null, null);
        testUser = userRepository.save(testUser);

        testBusiness = new Business(testUser.getId(), "Some Business", "Description", testAddressTwo, BusinessTypeService.ACCOMMODATION.toString(), LocalDate.now());
        testBusiness = businessRepository.save(testBusiness);

        testKeyword = new Keyword("Car", LocalDate.now());
        testKeyword = keywordRepository.save(testKeyword);
        List<Keyword> keywords = new ArrayList<>();
        keywords.add(testKeyword);

        testCard = new Card(testUser, "ForSale", LocalDateTime.now(), LocalDateTime.now().plusWeeks(2), "title", "card", keywords);
        testCard = cardRepository.save(testCard);

        testProduct = new Product(testBusiness.getId(), testBusiness, "product", "description", "manufacturer", 0.1, LocalDate.now());
        testProduct = productRepository.save(testProduct);

        testInventoryItem = new InventoryItem(testProduct, 1, 1.0, 1.0,
                LocalDate.of(2021, 5, 10), LocalDate.of(2022, 11, 11),
                LocalDate.of(2022, 10, 11), LocalDate.of(2022, 12, 11));
        testInventoryItem = inventoryRepository.save(testInventoryItem);

        testListing = new Listing(testInventoryItem, 1, 1.0, "more info",
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(5));
        testListing = listingRepository.save(testListing);

        testNotificationOne = new Notification()
                .withRecipient(testUser)
                .withMessage("Expiry Message")
                .withCard(testCard)
                .withType(NotificationType.CARD_EXPIRING)
                .withCreated(LocalDateTime.now());
        testNotificationOne = notificationRepository.save(testNotificationOne);

        testNotificationResponseOne = new NotificationResponse()
                .withCreated(testNotificationOne.getCreated())
                .withId(testNotificationOne.getId())
                .withReceipient(testNotificationOne.getRecipient().getId())
                .withMessage(testNotificationOne.getMessage())
                .withType(testNotificationOne.getType())
                .withCardId(testNotificationOne.getCard().getId());

        testNotificationTwo = new Notification()
                .withRecipient(testUser)
                .withCard(testCard)
                .withMessage("Comment Message")
                .withType(NotificationType.COMMENT_RECEIVED)
                .withCreated(LocalDateTime.now())
                .withSender(testUser);
        testNotificationTwo = notificationRepository.save(testNotificationTwo);

        testNotificationResponseTwo = new NotificationResponse()
                .withCreated(testNotificationTwo.getCreated())
                .withId(testNotificationTwo.getId())
                .withReceipient(testNotificationTwo.getRecipient().getId())
                .withMessage(testNotificationTwo.getMessage())
                .withType(testNotificationTwo.getType())
                .withSenderId(testNotificationTwo.getSender().getId())
                .withCardId(testNotificationTwo.getCard().getId());

        testNotificationThree = new Notification()
                .withRecipient(testUser)
                .withCreated(LocalDateTime.now())
                .withType(NotificationType.KEYWORD_ADDED)
                .withKeywordId(testKeyword.getId())
                .withMessage("Keyword Message");
        testNotificationThree = notificationRepository.save(testNotificationThree);

        testNotificationResponseThree = new NotificationResponse()
                .withCreated(testNotificationThree.getCreated())
                .withId(testNotificationThree.getId())
                .withReceipient(testNotificationThree.getRecipient().getId())
                .withMessage(testNotificationThree.getMessage())
                .withType(testNotificationThree.getType())
                .withKeywordId(testNotificationThree.getKeywordId());

        testNotificationFour = new Notification()
                .withRecipient(testUser)
                .withCreated(LocalDateTime.now())
                .withType(NotificationType.LIKED)
                .withMessage("Liked Message")
                .withListing(testListing);
        testNotificationFour = notificationRepository.save(testNotificationFour);

        testNotificationResponseFour = new NotificationResponse()
                .withCreated(testNotificationFour.getCreated())
                .withId(testNotificationFour.getId())
                .withReceipient(testNotificationFour.getRecipient().getId())
                .withMessage(testNotificationFour.getMessage())
                .withType(testNotificationFour.getType())
                .withListingId(testNotificationFour.getListing().getId());
    }

    /**
     * Testing for getting notification type expiry response
     */
    @Test
    void testGettingNotificationResponseWithCardExpiry() {
        NotificationResponse response = NotificationResponses.getResponse(testNotificationOne);

        Assertions.assertEquals(testNotificationResponseOne.getId(), response.getId());
        Assertions.assertEquals(testNotificationResponseOne.getCreated(), response.getCreated());
        Assertions.assertEquals(testNotificationResponseOne.getRecipientId(), response.getRecipientId());
        Assertions.assertEquals(testNotificationResponseOne.getMessage(), response.getMessage());
        Assertions.assertEquals(testNotificationResponseOne.getType(), response.getType());
        Assertions.assertEquals(testNotificationResponseOne.getCardId(), response.getCardId());
    }

    /**
     * Testing for getting notification type comment response
     */
    @Test
    void testGettingNotificationResponseWithCommentReceived() {
        NotificationResponse response = NotificationResponses.getResponse(testNotificationTwo);

        Assertions.assertEquals(testNotificationResponseTwo.getId(), response.getId());
        Assertions.assertEquals(testNotificationResponseTwo.getCreated(), response.getCreated());
        Assertions.assertEquals(testNotificationResponseTwo.getRecipientId(), response.getRecipientId());
        Assertions.assertEquals(testNotificationResponseTwo.getMessage(), response.getMessage());
        Assertions.assertEquals(testNotificationResponseTwo.getType(), response.getType());
        Assertions.assertEquals(testNotificationResponseTwo.getSenderId(), response.getSenderId());
        Assertions.assertEquals(testNotificationResponseTwo.getCardId(), response.getCardId());
    }

    /**
     * Testing for getting notification type keyword response
     */
    @Test
    void testGettingNotificationResponseWithKeywordAdded() {
        NotificationResponse response = NotificationResponses.getResponse(testNotificationThree);

        Assertions.assertEquals(testNotificationResponseThree.getId(), response.getId());
        Assertions.assertEquals(testNotificationResponseThree.getCreated(), response.getCreated());
        Assertions.assertEquals(testNotificationResponseThree.getRecipientId(), response.getRecipientId());
        Assertions.assertEquals(testNotificationResponseThree.getMessage(), response.getMessage());
        Assertions.assertEquals(testNotificationResponseThree.getType(), response.getType());
        Assertions.assertEquals(testNotificationResponseThree.getKeywordId(), response.getKeywordId());
    }

    /**
     * Testing for getting notification type liked response
     */
    @Test
    void testGettingNotificationResponseWithListingLiked() {
        NotificationResponse response = NotificationResponses.getResponse(testNotificationFour);

        Assertions.assertEquals(testNotificationResponseFour.getId(), response.getId());
        Assertions.assertEquals(testNotificationResponseFour.getCreated(), response.getCreated());
        Assertions.assertEquals(testNotificationResponseFour.getRecipientId(), response.getRecipientId());
        Assertions.assertEquals(testNotificationResponseFour.getMessage(), response.getMessage());
        Assertions.assertEquals(testNotificationResponseFour.getType(), response.getType());
        Assertions.assertEquals(testNotificationResponseFour.getListingId(), response.getListingId());
    }

    /**
     * Testing for getting notification responses
     */
    @Test
    void testGettingNotificationResponses() {
        List<NotificationResponse> check = new ArrayList<>();
        check.add(testNotificationResponseOne);
        check.add(testNotificationResponseTwo);
        check.add(testNotificationResponseThree);
        check.add(testNotificationResponseFour);

        List<Notification> notifications = new ArrayList<>();
        notifications.add(testNotificationOne);
        notifications.add(testNotificationTwo);
        notifications.add(testNotificationThree);
        notifications.add(testNotificationFour);

        List<NotificationResponse> responses = NotificationResponses.getAllResponses(notifications);

        Assertions.assertEquals(check.size(), responses.size());
        Assertions.assertEquals(check.get(0).getType(), responses.get(0).getType());
        Assertions.assertEquals(check.get(1).getType(), responses.get(1).getType());
        Assertions.assertEquals(check.get(2).getType(), responses.get(2).getType());
        Assertions.assertEquals(check.get(3).getType(), responses.get(3).getType());
    }

}
