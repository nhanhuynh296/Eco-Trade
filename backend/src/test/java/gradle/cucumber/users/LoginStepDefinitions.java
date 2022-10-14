package gradle.cucumber.users;


import gradle.cucumber.SpringIntegrationTest;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.seng302.main.models.Address;
import org.seng302.main.models.User;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.UserValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

/**
 * Contains step definitions for the user login feature (from users - U1.feature).
 */
public class LoginStepDefinitions extends SpringIntegrationTest {

    @Value("${server.port}")
    private int PORT;
    String email;
    String password;
    Integer userId;
    User user;

    @Autowired
    UserRepository userRepository;

    RequestSpecification request;
    private static Response response;

    @Before
    public void init() {
        RestAssured.port = PORT;
        email = "test@mail.com";
        password = "pass";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        LocalDate date = LocalDate.now();

        User user = new User();
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setEmail(this.email);
        user.setDateOfBirth(LocalDate.of(1970, 1, 1));
        user.setHomeAddress(new Address());
        user.setPassword(passwordEncoder.encode(this.password));
        user.setCreated(date);
        user.setUserRole("ROLE_USER");

        this.user = user;
    }

    @Given("I have registered an account with valid credentials")
    public void i_have_registered_an_account_with_valid_credentials() {
        request = RestAssured.given().log().uri();
        request.header("Content-Type", "application/json");
        User savedUser = userRepository.save(this.user);
        this.userId = Math.toIntExact(savedUser.getId());
    }

    @When("I login with registered valid email {string} and password {string}")
    public void i_login_with_registered_valid_email_and_password(String email, String password) {
        Assertions.assertEquals(true, UserValidateService.emailCheck(email));
        Assertions.assertNotNull(user.getEmail());
        Assertions.assertNotNull(user.getPassword());
    }

    @Then("I am logged into the website and status code is ok")
    public void i_am_logged_into_the_website_and_status_code_is_OK() {
        response = request.body("{ \"email\":\"" + email + "\", \"password\":\"" + password + "\"}")
                .post("/login");
        Assert.assertEquals(200, response.getStatusCode());
    }

    @When("I attempt to login with invalid {string} and {string} credential combinations")
    public void i_attempt_to_login_with_invalid_email_and_password_credential_combinations(String email, String password) {
        response = request.body("{ \"email\":\"" + email + "\", \"password\":\"" + password + "\"}")
                .post("/login");
    }

    @Then("I am not logged into the website and status code is bad request")
    public void i_am_not_logged_into_the_website_and_status_code_is_bad_request() {
        Assert.assertEquals(400, response.getStatusCode());
    }

    /**
     * Converts a HTTP response body to a JsonPath and returns the value at 'key', which is the new user id
     *
     * @param response HTTP Response body
     * @param key      the key for value to return (userId)
     * @return an Integer of the user id
     */
    public Long getUserId(Response response, String key) {
        String resp = response.asString();
        JsonPath js = new JsonPath(resp);
        return js.get(key);
    }
}