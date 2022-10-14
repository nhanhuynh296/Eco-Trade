package gradle.cucumber.cards;

import gradle.cucumber.SpringIntegrationTest;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.main.helpers.NotificationCategory;
import org.seng302.main.helpers.NotificationType;
import org.seng302.main.dto.request.AddressRequest;
import org.seng302.main.models.*;
import org.seng302.main.repository.CardRepository;
import org.seng302.main.repository.KeywordRepository;
import org.seng302.main.repository.NotificationRepository;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.repository.specificationHelper.CardSpecification;
import org.seng302.main.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CardsStepDefinitions extends SpringIntegrationTest {

    @Value("${server.port}")
    private int PORT;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    KeywordRepository keywordRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    CardService cardService;

    String cardTitle;
    String newTitle;
    String cardSection;

    Long userId;

    User testUser = new User("Juice", "", "Boy", "JB", "", "jb@juice.com",
            LocalDate.now(), "", new Address("", "", "", "", "", ""),
            LocalDate.now(), "ROLE_USER", "password123", "123", null);
    Card savedCard;
    Long savedCardId;

    // For contacting card owner story
    User sender;
    String message;
    RequestSpecification request;
    Response response;
    String userSession;

    Keyword newKeyword1;
    Keyword newKeyword2;
    List<Keyword> keywordList;

    Notification savedNotification;


    @Before
    public void init() {
        RestAssured.port = PORT;
        userRepository.deleteAll();

        sender = new User("Totally", "Real", "Name", "TRN", "Hello There",
                "abcdefgh@uclive.ac.nz", LocalDate.now(), "",
                new Address("", "", "", "", "", ""),
                LocalDate.now(), "ROLE_USER", passwordEncoder.encode("password123"), null, null);
    }

    @BeforeEach
    public void setup() {
        //remove all users from the repository
        keywordRepository.deleteAll();
        cardRepository.deleteAll();
        notificationRepository.deleteAll();
        userRepository.deleteAll();


        //add a test user to the repository
        userRepository.save(testUser);
    }

    //
    // U22: AC1 & AC2
    //

    @Given("I am logged in as user with no card")
    public void i_am_logged_in_as_user_with_no_card() {
        userRepository.save(testUser);
        userId = testUser.getId();
        Assertions.assertTrue(userRepository.existsById(userId));
    }

    @When("I create a card using title {string}, and section {string}")
    public void i_create_a_card_using_title_and_section(String title, String section) {
        List<Keyword> keywordList = new ArrayList<>();
        cardTitle = title;
        cardSection = section;
        userRepository.save(testUser);
        userId = testUser.getId();
        Card testCard = new Card(testUser, cardSection, LocalDateTime.now(), LocalDateTime.now(),
                cardTitle, "some description", keywordList);

        savedCard = cardRepository.save(testCard);
        savedCardId = savedCard.getId();
        Assertions.assertTrue(cardRepository.existsById(savedCardId));
    }

    @Then("The card is created with the correct title and section")
    public void the_card_is_created_with_the_correct_title_and_section() {
        Assertions.assertEquals(savedCard.getTitle(), cardTitle);
        Assertions.assertEquals(savedCard.getSection(), cardSection);
    }

    @And("The creatorId of the card is the id of the user who created the card")
    public void the_creatorId_of_the_card_is_the_id_of_the_user_who_created_the_card() {

        Assertions.assertEquals(userId, cardRepository.findById(savedCardId).get().getCreatorId());
    }

    //
    // U22: AC3
    //

    @Given("I am logged in as user with a card")
    public void i_am_logged_in_as_user_with_a_card() {
        userRepository.save(testUser);
        userId = testUser.getId();
        Card testCard = new Card(testUser, "Wanted", LocalDateTime.now(), LocalDateTime.now().plusWeeks(2), "Car",
                "some description", keywordList);
        savedCard = cardRepository.save(testCard);
        cardTitle = savedCard.getTitle();
        Assertions.assertEquals(savedCard.getCreatorId(), testUser.getId());
    }

    @When("I set the card title {string}")
    public void i_set_the_card_title(String title) {

        //Old title doesn't match title to be changed to
        Assertions.assertNotEquals(cardTitle, title);
        savedCard.setTitle(title);

        //After setting title, doesnt match old title
        Assertions.assertNotEquals(cardTitle, savedCard.getTitle());
        newTitle = savedCard.getTitle();
    }

    @Then("I expect the card to be saved with the new valid title")
    public void i_expect_the_card_to_be_saved_with_the_new_valid_title() {

        savedCard = cardRepository.save(savedCard);

        //Once saved ensure matches given title
        Assertions.assertEquals(savedCard.getTitle(), newTitle);
    }

    //
    // U22 AC5:
    //

    @When("I add valid keywords {string} and {string} to that card")
    public void i_add_valid_keywords_to_that_card(String keyWord1, String keyWord2) {
        keywordList = new ArrayList<>();

        newKeyword1 = new Keyword(keyWord1, LocalDate.now());
        newKeyword1 = keywordRepository.save(newKeyword1);

        keywordList.add(newKeyword1);

        newKeyword2 = new Keyword(keyWord2, LocalDate.now());
        newKeyword2 = keywordRepository.save(newKeyword2);

        keywordList.add(newKeyword2);

        savedCard.setKeywords(keywordList);
    }

    @Then("I expect the added keywords to be saved to the card")
    @Transactional
    public void i_expect_the_added_keyword_to_be_saved_to_the_card() {
        savedCard = cardRepository.save(savedCard);
        Assertions.assertEquals(savedCard.getKeywords().toString(), keywordList.toString());
    }

    //
    // Card expiry
    //

    @When("My card expiry date is less than {int} day")
    public void myCardExpiryDateIsLessThanDay(int day) {
        savedCard.setDisplayPeriodEnd(LocalDateTime.now().plusDays(day).minusSeconds(1));
        savedCard = cardRepository.save(savedCard);
    }

    @Then("I am notified or notification is created")
    public void iAmNotifiedOrNotificationIsCreated() {
        cardService.notifyNearExpireCard();
        savedCard = cardRepository.getCardById(savedCard.getId());
        Assertions.assertTrue(savedCard.isNotified());
    }

    //
    // Notification deletion
    //

    @Given("I am logged in as user with a notification")
    public void iAmLoggedInAsUserWithANotification() {
        userRepository.save(testUser);
        Card testCard = new Card(testUser, "Wanted", LocalDateTime.now(), LocalDateTime.now().plusWeeks(2), "Car",
                "some description", keywordList);
        savedCard = cardRepository.save(testCard);
        userId = testUser.getId();
        Notification testNotification = new Notification()
                .withRecipient(testUser)
                .withMessage("Your card is about to expire")
                .withType(NotificationType.CARD_EXPIRING)
                .withCreated(LocalDateTime.now());

        savedNotification = notificationRepository.save(testNotification);
        Assertions.assertEquals(savedNotification.getRecipient(), testUser);
    }

    @When("I delete the notification for the card")
    public void iDeleteTheNotificationForTheCard() {
        notificationRepository.delete(savedNotification);
        Assertions.assertEquals(savedNotification.getRecipient(), testUser);
    }

    @Then("The notification is deleted successfully")
    public void theNotificationIsDeletedSuccessfully() {
        Assertions.assertFalse(notificationRepository.existsById(savedNotification.getId()));
    }

    //
    // Card Search
    //

    @And("There is a card with title {string} and {string} in {string}")
    public void thereIsACardWithTitleAndKeyword(String title, String keyword, String section) {
        newKeyword1 = new Keyword(keyword, LocalDate.now());
        newKeyword1 = keywordRepository.save(newKeyword1);
        Card testCard = new Card(testUser, section, LocalDateTime.now(), LocalDateTime.now().plusWeeks(2), title,
                "some description", List.of(newKeyword1));
        savedCard = cardRepository.save(testCard);
        Assertions.assertNotNull(cardRepository.findById(savedCard.getId()));
        Assertions.assertEquals(savedCard.getTitle(), title);
        Assertions.assertEquals(savedCard.getSection(), section);
    }

    @When("I search for card with {string} in {string}")
    public void iSearchForCardWithKeyword(String keyword, String section) {
        // Does not do anything with database so just leave blank
    }

    @Then("I expect to see that card along with all the card with {string} in {string}")
    @Transactional
    public void iExpectToSeeThatCardAlongWithAllTheCardWithKeyword(String keyword, String section) {
        List<String> keywords = new ArrayList<>();
        keywords.add(keyword);

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setCountry("");
        addressRequest.setRegion("");
        addressRequest.setCity("");
        List<Card> cardList = cardRepository.findAll(Specification.where(CardSpecification.matchesAllFilters(keywords, section, "and", addressRequest)));
        boolean foundTitle = false;
        for (Card card : cardList) {
            if (!card.getSection().equals(section)) {
                Assertions.fail();
            }
            if (card.getTitle().equals(savedCard.getTitle())) { // Check if added card found
                foundTitle = true;
            }
            boolean found = false;
            for (Keyword cardKeyword : card.getKeywords()) {
                if (cardKeyword.getName().equals(keyword)) { // Check if card have our searched keyword
                    found = true;
                    break;
                }
            }
            if (!found) {
                Assertions.fail();
            }
        }
        if (!foundTitle) {
            Assertions.fail();
        }
    }

    //
    // Contacting Card Creators
    //

    @Given("I have a card titled {string} that someone can comment on")
    public void iHaveACardTitledThatSomeoneCanCommentOn(String title) {
        testUser = userRepository.save(testUser);
        sender = userRepository.save(sender);
        Card testCard = new Card(testUser, "Wanted", LocalDateTime.now(), LocalDateTime.now().plusWeeks(2), title,
                "some description", keywordList);
        savedCard = cardRepository.save(testCard);
        userId = testUser.getId();

        Assertions.assertTrue(userRepository.existsById(userId));
        Assertions.assertEquals(savedCard.getCreator(), testUser);
    }

    @When("A different user comments {string} on the card")
    public void aDifferentUserCommentsOnTheCard(String comment) {
        // Login
        request = RestAssured.given().log().uri();
        request.header("Content-Type", "application/json");
        response = request.body("{ \"email\":\"" + sender.getEmail() + "\", \"password\":\"" + "password123" + "\"}")
                .post("/login");
        userSession = response.getHeader("Set-Cookie").substring(11);

        // Make Comment
        Response response = request
                .header(new Header("content-type", "application/json"))
                .cookie(new Cookie.Builder("JSESSIONID", userSession).build())
                .body("{ \"recipientId\":\"" + testUser.getId() + "\", \"cardId\":\"" + savedCard.getId() + "\", \"message\":\"" + comment + "\"}")
                .post("/notifications/message")
                .andReturn();

        message =String.format("Community listing: '%s' %n", savedCard.getTitle()) +
                comment.trim().replaceAll("[\\t\\n\\r]+"," ");

        Assertions.assertEquals(201, response.getStatusCode());
    }

    @Then("I receive a notification that includes the correct message and the senders information")
    public void iReceiveANotificationWithCorrectMessage() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);

        Page<Notification> savedNotification = notificationRepository.findNotificationsByRecipientIdAndCategoryNotLike(testUser.getId(), NotificationCategory.ARCHIVED, pageable );
        Notification n = savedNotification.getContent().get(0);

        Assertions.assertEquals(message, n.getMessage());
        Assertions.assertEquals(sender.getId() , n.getSender().getId());


    }
}
