package org.seng302.main.serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.seng302.main.models.Address;
import org.seng302.main.models.Business;
import org.seng302.main.models.Product;
import org.seng302.main.models.User;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests for the ProductService class
 */
@SpringBootTest
class ProductServiceTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    private Product testProduct;
    private Address testAddress = new Address("3/24", "Ilam Road", "Christchurch",
            "Canterbury", "New Zealand", "90210");

    private Business testBusiness;

    Product product;

    String validName = "Food";
    String longName = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    String validDescription = "Delicious";
    String longDescription = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    LocalDate validCreated = LocalDate.of(2020, 12, 11);
    Double validRRP = 1.0;


    /**
     * Creates new inventory item object with valid attributes
     */
    @BeforeEach
    public void init() {
        User user = new User("A", "A", "A", "A", "A", "cja128@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "", "", "", ""), LocalDate.now(), "ROLE_USER", "password123", "55555", null);
        user = userRepository.save(user);

        testBusiness = new Business(user.getId(), "Lumbridge General Store", "Description1", testAddress, "Accomodation", LocalDate.now());
        testBusiness = businessRepository.save(testBusiness);

        product = new Product(testBusiness.getId(), testBusiness, validName, validDescription, null, validRRP, validCreated);

        user.setUserRole("ROLE_DEFAULT_ADMIN");
        testBusiness.setPrimaryAdministratorId(1L);
    }

    /**
     * Tests if a product is successfully validated
     */
    @Test
    @DisplayName("Test passes if a valid product passes validity checks")
    void testValidProduct() {
        assertEquals("", productService.isValidProduct(product));
    }

    /**
     * Tests if a product with no name is invalid
     */
    @Test
    @DisplayName("Test passes if a product with no name is invalid")
    void testEmptyProductName() {
        product.setName("");
        assertNotEquals("", productService.isValidProduct(product));
    }

    /**
     * Tests if a product with a name that is too long is invalid
     */
    @Test
    @DisplayName("Test passes if a product with a name that is too long is invalid")
    void testLongProductName() {
        product.setName(longName);
        assertNotEquals("", productService.isValidProduct(product));
    }

    /**
     * Tests if a product with a description that is too long is invalid
     */
    @Test
    @DisplayName("Test passes if a product with a description that is too long is invalid")
    void testLongProductDescription() {
        product.setDescription(longDescription);
        assertNotEquals("", productService.isValidProduct(product));
    }
}
