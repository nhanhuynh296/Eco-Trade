package org.seng302.main.responseFactoryTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.response.ProductResponse;
import org.seng302.main.dto.responsefactory.ProductResponses;
import org.seng302.main.models.*;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.ProductRepository;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.BusinessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Test product responses
 */
@SpringBootTest
class ProductResponsesTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    ProductRepository productRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    Business testBusiness;
    User testUser;
    Address testAddressOne;
    Address testAddressTwo;
    Product testProduct;

    ProductResponse testProductResponse;

    /**
     * Initialising all the variables before each test
     */
    @BeforeEach
    public void init() {
        testAddressOne = new Address("3/25", "Ilam Road", "Christchurch",
                "Canterbury", "New Zealand", "90210");
        testAddressTwo = new Address("3/24", "Ilam Road", "Christchurch",
                "Canterbury", "New Zealand", "90210");

        testUser = new User("John", "", "Johnson", "Johnny", "empty Bio", "email@here.com.co",
                LocalDate.of(1970, 1, 1), "0200 9020", testAddressOne, LocalDate.now(), "ROLE_USER",
                passwordEncoder.encode("pass"), null, null);
        testUser = userRepository.save(testUser);

        testBusiness = new Business(testUser.getId(), "Some Business", "Description", testAddressTwo, BusinessTypeService.ACCOMMODATION.toString(), LocalDate.now());
        testBusiness = businessRepository.save(testBusiness);

        testProduct = new Product(testBusiness.getId(), testBusiness, "product", "description", "manufacturer", 0.1, LocalDate.now());
        testProduct = productRepository.save(testProduct);

        testProductResponse = new ProductResponse();
        testProductResponse.setId(testProduct.getId());
        testProductResponse.setBusinessId(testBusiness.getId());
        testProductResponse.setName("product");
        testProductResponse.setDescription("description");
        testProductResponse.setManufacturer("manufacturer");
        testProductResponse.setRecommendedRetailPrice(0.1);
        testProductResponse.setPrimaryImageId(null);
        testProductResponse.setCreated(LocalDate.now());
        testProductResponse.setImages(null);
    }

    /**
     * Testing for getting product response
     */
    @Test
    void testGettingProductResponse() {
        ProductResponse response = ProductResponses.getResponse(testProduct);

        Assertions.assertEquals(testProductResponse.getId(), response.getId());
        Assertions.assertEquals(testProductResponse.getBusinessId(), response.getBusinessId());
        Assertions.assertEquals(testProductResponse.getName(), response.getName());
        Assertions.assertEquals(testProductResponse.getDescription(), response.getDescription());
        Assertions.assertEquals(testProductResponse.getManufacturer(), response.getManufacturer());
        Assertions.assertEquals(testProductResponse.getRecommendedRetailPrice(), response.getRecommendedRetailPrice());
        Assertions.assertEquals(testProductResponse.getPrimaryImageId(), response.getPrimaryImageId());
        Assertions.assertEquals(testProductResponse.getCreated(), response.getCreated());
        Assertions.assertEquals(testProductResponse.getImages(), response.getImages());
    }

    /**
     * Testing for getting product responses
     */
    @Test
    void testGettingProductResponses() {
        List<ProductResponse> check = new ArrayList<>();
        check.add(testProductResponse);

        List<Product> products = new ArrayList<>();
        products.add(testProduct);

        List<ProductResponse> responses = ProductResponses.getAllResponses(products);

        Assertions.assertEquals(check.size(), responses.size());
        Assertions.assertEquals(check.get(0).getId(), responses.get(0).getId());
    }

}
