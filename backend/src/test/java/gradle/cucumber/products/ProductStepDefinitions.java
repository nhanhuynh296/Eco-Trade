package gradle.cucumber.products;

import com.sun.xml.bind.v2.runtime.output.SAXOutput;
import gradle.cucumber.SpringIntegrationTest;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.antlr.v4.runtime.misc.LogManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.main.models.Address;
import org.seng302.main.models.Business;
import org.seng302.main.models.Product;
import org.seng302.main.models.User;
import org.seng302.main.repository.AddressRepository;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.ProductRepository;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Contains step definitions for registering a new user Cucumber tests (POST /users)
 */
@Transactional
public class ProductStepDefinitions extends SpringIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AddressRepository addressRepository;

    // Variables that are required for testing
    Address testAddress1 = new Address("3/24", "Ilam Road", "Christchurch",
            "Canterbury", "New Zealand", "90210");
    Address testAddress2 = new Address("3/24", "Ilam Road", "Christchurch",
            "Canterbury", "New Zealand", "90210");

    User testUser = new User("Juice", "", "Boy", "JB", "", "jb@juice.com",
            LocalDate.now(), "", testAddress1, LocalDate.now(), "ROLE_USER", "password123", "123", null);

    Business testBusiness;
    Long testBusinessId;

    Product savedProduct;
    LocalDate validCreated = LocalDate.of(2020, 12, 11);

    List<Product> businessProducts;

    @Before
    public void setup() {
        // removes data from the repositories
        productRepository.deleteAll();
        businessRepository.deleteAll();
        userRepository.deleteAll();

        testUser = userRepository.save(testUser);
        testBusiness = new Business(testUser.getId(), "Business", "cool business", testAddress2,
                "Accommodation", LocalDate.now());

        testBusiness = businessRepository.save(testBusiness);
        testBusinessId = testBusiness.getId();
    }

    // Logged in user acting as their business is able to view their product with name Apple Scenario
    @Given("I am logged in as an admin for a business with an existing catalogue of one product")
    public void i_am_logged_in_as_admin_for_business_with_catalogue_of_one_product() {
        savedProduct = productRepository.save(new Product(testBusiness.getId(), testBusiness, "Apple", "Apple is red",
                "Apple Company", 1.0, validCreated));
    }

    @When("I request the products in the catalogue")
    public void i_request_the_products_in_the_catalogue() {
        Page<Product> businesses = productRepository.findProductsByBusinessId(testBusinessId, null);
        businessProducts = businesses.getContent();
    }

    @Then("I can view my business's product with name {string}")
    public void i_can_view_the_product_with_name(String name) {
        Assertions.assertEquals(1, businessProducts.size());
        Assertions.assertEquals(testBusinessId, businessProducts.get(0).getBusiness().getId());
        Assertions.assertEquals(name, businessProducts.get(0).getName());
    }

    // Logged in user acting as their business is able to add two new products and check that they were added Scenario
    @Given("I have added two valid products")
    public void i_have_added_two_valid_products() {
        Product product1 = new Product(testBusiness.getId(), testBusiness, "Apple", "Apple is red", null, 1.0, validCreated);
        Product product2 = new Product(testBusiness.getId(), testBusiness, "Banana", "Banana is yellow", null, 1.0, validCreated);

        productRepository.save(product1);
        productRepository.save(product2);
    }

    @Then("The products are in the catalogue")
    public void the_products_are_in_the_catalogue() {
        boolean appleFound = false;
        boolean bananaFound = false;

        for (Product p : businessProducts) {
            if (p.getName().equals("Apple")) {
                appleFound = true;
            } else if (p.getName().equals("Banana")) {
                bananaFound = true;
            }
        }

        Assertions.assertTrue(appleFound);
        Assertions.assertTrue(bananaFound);
    }

    // User acting as a business is able to add a new product and check the product details Scenario
    @Then("The product has the mandatory values with {string}, {string}, {string}")
    public void theProductHasTheMandatoryValues(String name, String description, String manufacturer) {
        Product currentProduct = businessProducts.get(0);

        Assertions.assertEquals(name, currentProduct.getName());
        Assertions.assertEquals(description, currentProduct.getDescription());
        Assertions.assertEquals(manufacturer, currentProduct.getManufacturer());
    }
}
