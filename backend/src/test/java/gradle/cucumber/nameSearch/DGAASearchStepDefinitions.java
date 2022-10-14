package gradle.cucumber.nameSearch;

import com.sipios.springsearch.SearchCriteria;
import com.sipios.springsearch.SpecificationImpl;
import com.sipios.springsearch.anotation.SearchSpec;
import gradle.cucumber.SpringIntegrationTest;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.seng302.main.DGAAHelper;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.dto.response.UserResponse;
import org.seng302.main.models.User;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;


public class DGAASearchStepDefinitions extends SpringIntegrationTest {

    @Value("${server.port}")
    private int PORT;

    private final String DGAAEmail = "test@admin.com";

    private final String DGAAPassword = "test";

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    private RequestSpecification request;
    private static Response response;

    private String foundUserRole;

    private User currentUser;

    @Before
    public void init() {
        RestAssured.port = PORT;
    }

    @Given("I am logged in with global admin privileges")
    public void iAmLoggedInWithGlobalAdminPrivileges() {
        request = RestAssured.given().log().uri();
        request.header("Content-Type", "application/json");

        userRepository.deleteByUserRole("ROLE_DEFAULT_ADMIN");

        /* This test runs too fast, gotta manually add a DGAA for this cucumber test */
        DGAAHelper dgaaHelper = new DGAAHelper(userRepository, this.DGAAEmail, this.DGAAPassword);
        dgaaHelper.addDGAA();

        String jsonString = "{ \"email\":\"" + DGAAEmail + "\", \"password\":\"" + DGAAPassword + "\"}";
        response = request.body(jsonString)
                .post("/login");

        currentUser = userRepository.findByUserRole("ROLE_DEFAULT_ADMIN").get(0);

        Assertions.assertEquals(200, response.getStatusCode());
    }

    @Transactional
    @When("I search for myself on the user search page")
    public void iSearchForMyselfOnTheUserSearchPage() {

        SearchCriteria searchCriteria = new SearchCriteria("firstName", ":", "", "DGAA", "");

        SearchSpec annotation = new SearchSpec() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return SearchSpec.class;
            }

            @Override
            public boolean caseSensitiveFlag() {
                return false;
            }

            @Override
            public String searchParam() {
                return null;
            }
        };

        Specification<User> specs = new SpecificationImpl<>(searchCriteria, annotation);
        PaginationInfo<UserResponse> results = userService.userSearch(specs, currentUser, 0, 10, "FIRST_DESC");

        foundUserRole = results.getPaginationElements().get(0).getRole();
    }

    @Then("the webpage displays my account in the results")
    public void theWebpageDisplaysMyAccountInTheResults() {
        Assertions.assertEquals("ROLE_DEFAULT_ADMIN", foundUserRole);
    }
}
