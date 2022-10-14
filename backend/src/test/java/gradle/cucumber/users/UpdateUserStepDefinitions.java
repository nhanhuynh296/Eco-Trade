package gradle.cucumber.users;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.seng302.main.models.Address;
import org.seng302.main.models.User;
import org.seng302.main.repository.AddressRepository;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;


public class UpdateUserStepDefinitions {

    @Value("${server.port}")
    private int PORT;

    private User user;
    private Cookie cookie;
    private JSONObject payload;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    RequestSpecification request;
    private static Response response;

    @Before
    public void init() {
        RestAssured.port = PORT;
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        user = new User("Jeff", "Real", "Bezos", "TRN", "Hello There",
                "jeffbezos@mail.com", LocalDate.now().minusYears(20), "",
                new Address("", "", "", "", "Valid", ""),
                LocalDate.now(), "ROLE_USER", passwordEncoder.encode("password123"), null, null);

        payload = getPayload();

    }


    @Given("I am logged in")
    public void i_am_logged_in() {
        this.user = userRepository.save(user);

        request = RestAssured.given().log().uri();
        request.header("Content-Type", "application/json");
        response = request.body("{ \"email\":\"" + user.getEmail() + "\", \"password\":\"" + "password123" + "\"}")
                .post("/login");
        Assert.assertEquals(200, response.getStatusCode());
        this.cookie = new Cookie.Builder("JSESSIONID", response.getHeader("Set-Cookie").substring(11)).build();
    }

    @Given("my name is {string} {string}")
    public void my_name_is(String firstName, String lastName) {
        Assert.assertEquals(firstName, this.user.getFirstName());
        Assert.assertEquals(lastName, this.user.getLastName());
    }

    @When("I change my first name to {string}, and my last name to {string}")
    public void i_change_my_first_name_to_and_my_last_name_to(String newFirstName, String newLastName) {
        payload.put("firstName", newFirstName);
        payload.put("lastName", newLastName);

        response = request.body(payload)
                .cookie(this.cookie)
                .put("/users/" + this.user.getId());
    }

    @Then("my name is updated successfully to {string} {string}")
    public void my_name_is_updated_successfully(String newFirstName, String newLastName) {
        Assert.assertEquals(200, response.getStatusCode());
        this.user = userRepository.findUserById(this.user.getId());
        Assert.assertEquals(newFirstName, this.user.getFirstName());
        Assert.assertEquals(newLastName, this.user.getLastName());
    }

    @Given("my email is {string}")
    public void my_email_is(String email) {
        Assert.assertEquals(email, this.user.getEmail());
    }

    @When("I change my email to {string}")
    public void i_change_my_email_to(String newEmail) {
        payload.put("email", newEmail);
        response = request.body(payload)
                .cookie(this.cookie)
                .put("/users/" + this.user.getId());
        Assert.assertEquals(200, response.getStatusCode());
    }

    @Then("my email is updated successfully to {string}")
    public void my_email_is_updated_successfully(String email) {
        Assert.assertEquals(200, response.getStatusCode());

        this.user = userRepository.findUserById(this.user.getId());
        Assert.assertEquals(email, this.user.getEmail());
    }

    @When("I try to remove my email")
    public void i_try_to_remove_my_email() {
        payload.remove("email");
        response = request.body(payload)
                .cookie(this.cookie)
                .put("/users/" + this.user.getId());
    }

    @Then("I am returned an error as email is mandatory")
    public void i_am_returned_an_error_as_email_is_mandatory() {
        Assert.assertEquals(400, response.getStatusCode());
    }

    private JSONObject getPayload() {
        JSONObject payload = new JSONObject();
        payload.put("firstName", this.user.getFirstName());
        payload.put("middleName", this.user.getMiddleName());
        payload.put("lastName", this.user.getLastName());
        payload.put("nickname", this.user.getNickname());
        payload.put("bio", this.user.getBio());
        payload.put("email", this.user.getEmail());
        payload.put("dateOfBirth", this.user.getDateOfBirth());
        payload.put("phoneNumber", this.user.getPhoneNumber());
        payload.put("homeAddress", this.user.getHomeAddress());
        payload.put("password", "password123");
        return payload;
    }
}
