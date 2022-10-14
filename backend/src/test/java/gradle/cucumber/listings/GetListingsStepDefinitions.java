package gradle.cucumber.listings;

import gradle.cucumber.SpringIntegrationTest;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.seng302.main.models.*;
import org.seng302.main.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

public class GetListingsStepDefinitions extends SpringIntegrationTest {

    @Value("${server.port}")
    private int PORT;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    private final LocalDate localDate = LocalDate.now();


    RequestSpecification request;
    Response response;
    String userSession;
    User user;

    // Address for business
    private final Address validAddress = new Address("3/24", "Ilam Road", "Christchurch",
            "Canterbury", "New Zealand", "90210");

    // Valid business entity for testing product catalogue
    private Business testBusinessOne;

    private InventoryItem invItem;
    private Listing listing;
    private Long id;


    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Before
    public void init() {
        RestAssured.port = PORT;
        userRepository.deleteAll();

        user = new User("User", "For", "Keywords", "UFK", "Hello There",
                "abcdefgh@uclive.ac.nz", LocalDate.now(), "",
                new Address("", "", "", "", "New Zealand", ""),
                LocalDate.now(), "ROLE_USER", passwordEncoder.encode("password123"), null, null);
            }

    @Given("I am logged in as a user on the listings page")
    public void i_am_logged_in_as_a_user_on_the_listings_page() {
        user = userRepository.save(user);

        testBusinessOne = new Business(user.getId(), "Varrock Grand Exchange", "Description1", validAddress, "Accomodation", localDate);
        testBusinessOne = businessRepository.save(testBusinessOne);

        Product product = new Product(testBusinessOne.getId(), testBusinessOne, "Some Product", "Description",
                "TestCo.", 12d, LocalDate.now());
        product = productRepository.save(product);

        invItem = new InventoryItem(product, 10, 1d, 10d, null, null, null, LocalDate.now().plusDays(2));
        invItem = inventoryRepository.save(invItem);
        listing = new Listing(invItem, 5, 5.5, "Lots of information", localDate, LocalDate.now().plusDays(1));

        // Login
        request = RestAssured.given().log().uri();
        request.header("Content-Type", "application/json");
        response = request.body("{ \"email\":\"" + user.getEmail() + "\", \"password\":\"" + "password123" + "\"}")
                .post("/login");
        userSession = response.getHeader("Set-Cookie").substring(11);

        Assertions.assertNotNull(userRepository.findUserById(user.getId()));
        // Save Listing
        listing = listingRepository.save(listing);
    }

    @When("I click on a listing")
    public void i_click_on_a_listing() {
        Response response = request
                .cookie(new Cookie.Builder("JSESSIONID", userSession).build())
                .get("/listings/" + listing.getId())
                .andReturn();

        id = response.jsonPath().getLong("id");
    }

    @Then("Information relating only to that listing is displayed")
    public void information_relating_only_to_that_listing_is_displayed() {
        Assertions.assertEquals(listing.getId(), id);
    }

}
