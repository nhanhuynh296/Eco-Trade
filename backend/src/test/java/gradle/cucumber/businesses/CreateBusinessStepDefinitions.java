package gradle.cucumber.businesses;

import gradle.cucumber.SpringIntegrationTest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.main.models.Address;
import org.seng302.main.models.Business;
import org.seng302.main.models.User;
import org.seng302.main.repository.AddressRepository;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

public class CreateBusinessStepDefinitions extends SpringIntegrationTest {

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    // Variables that are required for testing
    Address testAddress1 = new Address("3/24", "Ilam Road", "Christchurch",
            "Canterbury", "New Zealand", "90210");
    Address testAddress2 = new Address("3/24", "Ilam Road", "Christchurch",
            "Canterbury", "New Zealand", "90210");

    User testUser = new User("Juice", "", "Boy", "JB", "", "jb@juice.com",
            LocalDate.now(), "", testAddress1, LocalDate.now(), "ROLE_USER", "password123", "123", null);
    Long testUserId;

    String businessName;
    String businessDescr;
    String businessType;

    Business savedBusiness;
    Long savedBusinessId;

    List<Business> userBusinesses;

    @BeforeEach
    public void setup() {
        // removes data from the repository
        businessRepository.deleteAll();
    }

    @Given("I am logged in as user")
    public void i_am_logged_in_as_user() {
        testUser = userRepository.save(testUser);
        testUserId = testUser.getId();
    }

    @When("I create a business account using name {string}, description {string}, businessType {string}")
    public void i_create_a_business_account_using_name_description_address_businessType(String name, String description, String type) {
        if (!name.isBlank() && !description.isBlank() && !type.isBlank()) {
            businessName = name;
            businessDescr = description;
            businessType = type;

            Business business = new Business(testUserId, businessName, businessDescr, testAddress2, businessType, LocalDate.now());

            savedBusiness = businessRepository.save(business);
            savedBusinessId = savedBusiness.getId();
        }
    }

    // Valid Business Scenario
    @Then("The business account is created with the correct name, description and business type")
    public void the_business_account_is_created_with_the_correct_name_description_address_and_business_type() {
        Assertions.assertEquals(savedBusiness.getName(), businessName);
        Assertions.assertEquals(savedBusiness.getDescription(), businessDescr);
        Assertions.assertEquals(savedBusiness.getBusinessType(), businessType);
    }

    @And("The primary admin of the business is the user who created the account")
    public void the_primary_admin_of_the_business_is_the_user_who_created_the_account() {
        Assertions.assertEquals(savedBusiness.getPrimaryAdministratorId(), testUserId);
    }

    // Invalid Business Scenario
    @Then("The business account is not created")
    public void the_business_account_is_not_created() {
        int count = businessRepository.findBusinessesByPrimaryAdministratorId(testUserId).size();
        Assertions.assertEquals(0, count);
    }

    // Viewing all user's businesses Scenario
    @Given("I am logged in as user with one business")
    public void i_am_logged_in_as_user_with_one_business() {
        testUser = userRepository.save(testUser);
        testUserId = testUser.getId();

        Business business = new Business(testUserId, "Lumbridge", "Adventuring store", testAddress2, "Accommodation", LocalDate.now());
        businessRepository.save(business);
    }

    @When("I can request to view all of my businesses")
    public void i_can_request_to_view_all_of_my_businesses() {
        userBusinesses = businessRepository.findBusinessesByPrimaryAdministratorId(testUserId);
    }

    @Then("I should be able to see my {string} business")
    public void i_should_be_able_to_see_my_business(String name) {
        Assertions.assertEquals(1, userBusinesses.size());
        Assertions.assertEquals(name, userBusinesses.get(0).getName());
    }
}
