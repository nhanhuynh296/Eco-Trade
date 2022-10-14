package gradle.cucumber.users;

import gradle.cucumber.SpringIntegrationTest;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.seng302.main.models.Address;
import org.seng302.main.models.User;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.UserValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

/**
 * Contains step definitions for registering a new user Cucumber tests (POST /users)
 */
public class RegisterStepDefinitions extends SpringIntegrationTest {
    @Value("${server.port}")
    private int PORT;

    private String email;

    @Autowired
    UserRepository userRepository;

    RequestSpecification request;
    private static Response response;

    @Before
    public void init() {
        RestAssured.port = PORT;
    }

    @Given("I am not logged into the website")
    public void i_am_not_logged_into_the_website() {
        request = RestAssured.given().log().uri();
        request.header("Content-Type", "application/json");
    }

    @When("I provide valid credentials {string}, {string}, {string}, {string}, {string}, {string}, {string}, {string}, {string}, {string}")
    public void provideValidCredentials(String firstName,
                                        String middleName,
                                        String lastName,
                                        String nickname,
                                        String bio,
                                        String email,
                                        String dateOfBirth,
                                        String phoneNumber,
                                        String homeAddress,
                                        String password) {

        createHTTP409User();
        String[] splitAddress = homeAddress.split(", ");
        Address address = CreateUserStepDefinitions.stringAddressToEntity(splitAddress);

        this.email = email;

        JSONObject entity = new JSONObject();
        entity.put("firstName", firstName);
        entity.put("middleName", middleName);
        entity.put("lastName", lastName);
        entity.put("nickname", nickname);
        entity.put("bio", bio);
        entity.put("email", email);
        entity.put("dateOfBirth", dateOfBirth);
        entity.put("phoneNumber", phoneNumber);
        entity.put("homeAddress", address);
        entity.put("password", password);
        response = request.body(entity)
                .post("/users");
    }

    @Then("I am taken to my home page")
    public void i_am_taken_to_my_home_page() {
        Assert.assertEquals(201, response.getStatusCode());

    }

    @And("logged in as the correct user")
    public void logged_in_as_the_correct_user() {
        String resp = response.asString();
        JsonPath js = new JsonPath(resp);
        Integer newUserId = js.get("userId");
        Assert.assertEquals(this.email, userRepository.findUserById(newUserId.longValue()).getEmail());
    }

    @When("I provide nulls in any of the required fields {string}, {string}, {string}, {string}, {string}, {string}, {string}, {string}, {string}, {string}")
    public void provideInvalidCredentials(String firstName,
                                          String middleName,
                                          String lastName,
                                          String nickname,
                                          String bio,
                                          String email,
                                          String dateOfBirth,
                                          String phoneNumber,
                                          String homeAddress,
                                          String password) {

        createHTTP409User();

        //Current work around for cucumber is to use strings in the feature folder and just convert them to the required type
        LocalDate date;
        LocalDate created = LocalDate.now();
        try {
            date = LocalDate.parse(dateOfBirth);
        } catch (Exception e) {
            date = null;
        }
        Address address;
        String[] splitAddress = homeAddress.split(", ");
        if (splitAddress.length == 5) {
            address = CreateUserStepDefinitions.stringAddressToEntity(splitAddress);
        } else {
            address = new Address();
        }

        User user = new User(firstName, middleName, lastName, nickname, bio, email, date, phoneNumber, address, created, "User", password, "", null);
        Assert.assertFalse(UserValidateService.checkRequired(user));

        JSONObject entity = new JSONObject();
        response = request.body(entity)
                .post("/users");
    }

    @Then("I am not taking to my home page and remain on the register page")
    public void i_am_NOT_taken_to_my_home_page() {
        Assert.assertEquals(400, response.getStatusCode());
    }

    /**
     * Create a duplicate user on purpose for the HTTP 409 response test
     */
    private void createHTTP409User() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Address validAddress1 = new Address("3/24", "Ilam Road", "Christchurch", "Canterbury", "New Zealand"
                , "90210");
        User userForHTTP409 = new User("John", "", "Johnson", "Johnny", "empty Bio", "john@gmail.com",
                LocalDate.of(1970, 1, 1), "0200 9020", validAddress1, LocalDate.now(), "ROLE_USER",
                passwordEncoder.encode("pass"), "3201", null);
        userRepository.save(userForHTTP409);
    }
}
