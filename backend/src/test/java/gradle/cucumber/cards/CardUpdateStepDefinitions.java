package gradle.cucumber.cards;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.seng302.main.dto.request.CardRequest;
import org.seng302.main.models.Address;
import org.seng302.main.models.Card;
import org.seng302.main.models.Keyword;
import org.seng302.main.models.User;
import org.seng302.main.repository.AddressRepository;
import org.seng302.main.repository.CardRepository;
import org.seng302.main.repository.KeywordRepository;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class CardUpdateStepDefinitions {

    @Value("${server.port}")
    private int PORT;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    KeywordRepository keywordRepository;

    User user;
    Cookie cookie;

    CardRequest cardRequest = new CardRequest();
    Card card;
    String oldTitle;
    String oldKeyword;
    Keyword keyword;

    ArrayList<String> newKeywords = new ArrayList<>();
    List<Keyword> cardKeywords = new ArrayList<>();

    RequestSpecification request;
    private static Response response;

    @Before
    public void init() {
        RestAssured.port = PORT;
        userRepository.deleteAll();
        cardRepository.deleteAll();
        keywordRepository.deleteAll();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        user = new User("Jeff", "Real", "Bezos", "TRN", "Hello There",
                "jeffbezos@mail.com", LocalDate.now().minusYears(20), "",
                new Address("", "", "", "", "Valid", ""),
                LocalDate.now(), "ROLE_USER", passwordEncoder.encode("password123"), null, null);

        card = new Card(user, "ForSale", LocalDateTime.now(), LocalDateTime.now(),
                "Old card title", "Old card description", cardKeywords);
    }

    @Given("I am logged in with a card")
    public void i_am_logged_in_with_a_card() {
        keyword = new Keyword("Car", LocalDate.now());
        keyword = keywordRepository.save(keyword);

        user = userRepository.save(user);

        cardKeywords.add(keyword);
        card.setKeywords(cardKeywords);
        card = cardRepository.save(card);

        request = RestAssured.given().log().uri();
        request.header("Content-Type", "application/json");
        response = request.body("{ \"email\":\"" + user.getEmail() + "\", \"password\":\"" + "password123" + "\"}")
                .post("/login");
        Assert.assertEquals(200, response.getStatusCode());
        cookie = new Cookie.Builder("JSESSIONID", response.getHeader("Set-Cookie").substring(11)).build();

        Assertions.assertTrue(cardRepository.existsById(card.getId()));
        Assertions.assertEquals(card.getCreator().getId(), user.getId());
    }

    @Given("The card title is {string}")
    public void the_card_title_is(String title) {
        oldTitle = title;
        Assert.assertEquals(oldTitle, card.getTitle());
    }

    @When("I change the card title to {string}")
    public void i_change_the_card_title(String newTitle) {
        cardRequest.setTitle(newTitle);

        response = request.body(cardRequest)
                .cookie(cookie)
                .put("/cards/" + card.getId());
    }

    @Then("The card is updated successfully with title {string}")
    public void the_card_is_updated_successfully(String newTitle) {
        card = cardRepository.getCardById(card.getId());
        Assertions.assertNotEquals(oldTitle, newTitle);
        Assertions.assertEquals(card.getTitle(), newTitle);
    }

    @Given("The card has keyword {string}")
    public void the_card_has_keyword(String keywordName) {
        oldKeyword = keywordName;
        Keyword keyword = keywordRepository.findByName(keywordName);
        Assertions.assertEquals(card.getKeywords().get(0).getId(), keyword.getId());
    }

    @When("I add a keyword {string}")
    @Transactional
    public void i_add_a_keyword(String keywordName) {

        newKeywords.add(oldKeyword);
        newKeywords.add(keywordName);
        cardRequest.setKeywords(newKeywords);

        request = RestAssured.given().log().uri();
        request.header("Content-Type", "application/json");
        response = request.body(cardRequest)
                .cookie(cookie)
                .put("/cards/" + card.getId());
        Assertions.assertEquals(200, response.getStatusCode());
    }

    @Then("The card is updated successfully with both keywords {string} and {string}")
    @Transactional
    public void the_card_is_updated_successfully_with_both_keywords(String keywordOne, String keywordTwo) {
        card = cardRepository.getCardById(card.getId());
        Keyword car = keywordRepository.findByName(keywordOne);
        Keyword vehicle = keywordRepository.findByName(keywordTwo);
        Assertions.assertEquals(card.getKeywords().get(0), car);
        Assertions.assertEquals(card.getKeywords().get(1), vehicle);
    }
}