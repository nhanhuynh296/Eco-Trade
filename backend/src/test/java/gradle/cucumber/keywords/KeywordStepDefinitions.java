package gradle.cucumber.keywords;

import gradle.cucumber.SpringIntegrationTest;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.jupiter.api.Assertions;
import org.seng302.main.dto.response.KeywordResponse;
import org.seng302.main.models.Address;
import org.seng302.main.models.Card;
import org.seng302.main.models.Keyword;
import org.seng302.main.models.User;
import org.seng302.main.repository.CardRepository;
import org.seng302.main.repository.KeywordRepository;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class KeywordStepDefinitions extends SpringIntegrationTest {

    @Value("${server.port}")
    private int PORT;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private KeywordService keywordService;

    private List<KeywordResponse> searchedKeywords;

    User user;

    RequestSpecification request;
    Response response;
    String userSession;

    Long keywordId;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    Keyword keyword;

    Card card;

    @Before
    public void init() {
        RestAssured.port = PORT;
        userRepository.deleteAll();
        cardRepository.deleteAll();
        keywordRepository.deleteAll();

        keywordId = null;

        user = new User("User", "For", "Keywords", "UFK", "Hello There",
                "abcdefgh@uclive.ac.nz", LocalDate.now(), "",
                new Address("", "", "", "", "New Zealand", ""),
                LocalDate.now(), "ROLE_USER", passwordEncoder.encode("password123"), null, null);

    }

    @Given("I am logged in as a user")
    public void iAmLoggedInAsAUser() {
        user = userRepository.save(user);
        // Login
        request = RestAssured.given().log().uri();
        request.header("Content-Type", "application/json");
        response = request.body("{ \"email\":\"" + user.getEmail() + "\", \"password\":\"" + "password123" + "\"}")
                .post("/login");
        userSession = response.getHeader("Set-Cookie").substring(11);

        Assertions.assertNotNull(userRepository.findUserById(user.getId()));
    }

    @When("I create a new keyword with name {string}")
    public void iCreateANewKeyword(String name) {
        JSONObject data = new JSONObject();
        data.put("name", name);

        Response response = request
                .body(data.toJSONString())
                .cookie(new Cookie.Builder("JSESSIONID", userSession).build())
                .post("/keywords")
                .andReturn();

        keywordId = response.jsonPath().getLong("id");

        Assert.assertEquals(201, response.getStatusCode());
    }

    @Then("The keyword is successfully saved")
    public void theKeywordIsSuccessfullySaved() {
        Assertions.assertTrue(keywordRepository.existsById(keywordId));
    }

    @Given("There are already existing keywords with the letter {string} \\(Case insensitive)")
    public void thereAreAlreadyExistingKeywordsWithTheLetterCaseInsensitive(String letter) {
        String[] keywords = new String[] {
                "Truck", "Fruit", "Lemon", "Animal",
                "Dog", "Cat", "Truck2", letter
        };

        keywordRepository.deleteAll();
        for (String keyword : keywords) {
            keywordRepository.save(new Keyword(keyword, LocalDate.now()));
        }
    }

    @When("I search for keywords containing the letter {string}")
    public void iSearchForKeywordsContainingTheLetter(String letter) {
        searchedKeywords = keywordService.getAllKeywordsLike(letter);
    }

    @Then("I am presented with all the keywords that contain the letter {string}")
    public void iAmPresentedWithAllTheKeywordsThatContainTheLetter(String letter) {
        boolean hasLetter = false;
        for (KeywordResponse response : searchedKeywords) {
            if (response.getName().contains(letter)) {
                hasLetter = true;
                break;
            }
        }

        Assertions.assertTrue(hasLetter);
    }

    @Given("I am logged in as an administrator")
    public void iAmLoggedInAsAnAdministrator() {
        user.setUserRole("ROLE_ADMIN");
        user = userRepository.save(user);
        Assertions.assertEquals("ROLE_ADMIN", user.getUserRole());
    }

    @And("There is a card title {string} with keyword {string}")
    public void thereIsACardTitleWithTag(String cardTitle, String keywordName) {
        keyword = keywordRepository.save(new Keyword(keywordName, LocalDate.now()));
        List<Keyword> keywordList = List.of(keyword);
        card = new Card(user, "ForSale", LocalDateTime.now(), LocalDateTime.now().plusWeeks(2), cardTitle, "Description", keywordList);
        card = cardRepository.save(card);
        Assertions.assertEquals(cardTitle, card.getTitle());
        Assertions.assertEquals(keywordName, keyword.getName());
    }

    @When("I delete the keyword {string}")
    public void iDeleteTheTag(String keywordName) {
        Assume.assumeTrue(keywordName.equals(keyword.getName()));
        keywordService.deleteKeyword(keyword.getId());

    }

    @Then("The keyword {string} was removed")
    public void theTagWasRemovedFromTheDatabase(String keywordName) {
        Assume.assumeTrue(keywordName.equals(keyword.getName()));
        Assertions.assertFalse(keywordRepository.existsKeywordByName(keywordName));
    }

    @And("The keyword {string} was remove from all the cards")
    @Transactional
    public void theTagWasRemoveFromTheCard(String keywordName) {
        Assume.assumeTrue(keywordName.equals(keyword.getName()));
        card = cardRepository.getCardById(this.card.getId());
        for (Keyword keyword : card.getKeywords()) {
            if (keyword.getName().equals(keywordName)) {
                Assertions.fail();
            }
        }
    }
}
