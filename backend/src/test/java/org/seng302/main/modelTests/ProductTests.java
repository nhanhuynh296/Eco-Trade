package org.seng302.main.modelTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.models.*;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;


@SpringBootTest
@AutoConfigureMockMvc
class ProductTests {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    private Product testProduct;
    private Address testAddress = new Address("3/24", "Ilam Road", "Christchurch",
                                                    "Canterbury", "New Zealand", "90210");

    private Business testBusiness;


    @BeforeEach
    public void init() {
        User user = new User("A", "A", "A", "A", "A", "cja128@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "", "", "New Zealand", ""), LocalDate.now(), "ROLE_USER", "password123", "55555", null);
        user = userRepository.save(user);

        testBusiness = new Business(user.getId(), "Lumbridge General Store", "Description1", testAddress, "Accomodation", LocalDate.now());
        testBusiness = businessRepository.save(testBusiness);

        // User with all required fields with valid attributes
        testProduct = new Product(testBusiness.getId(), testBusiness,
                "Watties Baked Beans",
                "Baked beans in a can",
                "Heinz Wattie's Limited",
                2.2,
                LocalDate.now()
        );
    }

    /**
     * Tests that the validateProduct function returns correct error message
     * when the name of a product is an empty string
     */
    @Test
    void testInvalidEmptyProductName() {
        boolean errorThrown = false;
        String message = null;

        // Make the name invalid
        testProduct.setName("");

        try {
            ProductValid.validateProduct(testProduct);
        } catch (Exception e) {
            message = e.getMessage();
            errorThrown = true;
        }
        assert (errorThrown);
        Assertions.assertEquals("400 BAD_REQUEST \"Product name cannot be empty.\"", message);

    }

    /**
     * Tests that the validateProduct function returns the correct error
     * when the product name is too long
     */
    @Test
    void testInvalidTooLongProductName() {
        boolean errorThrown = false;
        String message = null;
        String invalidName = new String(new char[71]).replace("\0", "A");


        // Make the name invalid
        testProduct.setName(invalidName);

        try {
            ProductValid.validateProduct(testProduct);
        } catch (Exception e) {
            message = e.getMessage();
            errorThrown = true;
        }
        assert (errorThrown);
        Assertions.assertEquals("400 BAD_REQUEST \"Product name length exceeds the max length of 70.\"", message);

    }

    /**
     * Tests that the validateProduct function returns the correct error
     * when the product descriptions is too long
     */
    @Test
    void testInvalidTooLongProductDescription() {
        boolean errorThrown = false;
        String message = null;
        String invalidName = new String(new char[251]).replace("\0", "A");

        // Make the name invalid
        testProduct.setDescription(invalidName);

        try {
            ProductValid.validateProduct(testProduct);
        } catch (Exception e) {
            message = e.getMessage();
            errorThrown = true;
        }
        assert (errorThrown);
        Assertions.assertEquals("400 BAD_REQUEST \"Product description exceeds max character length of 250.\"", message);
    }


}
