package org.seng302.main.responseFactoryTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.response.InventoryItemResponse;
import org.seng302.main.dto.responsefactory.InventoryItemResponses;
import org.seng302.main.dto.responsefactory.ProductResponses;
import org.seng302.main.models.*;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.InventoryRepository;
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
 * Test inventory item responses
 */
@SpringBootTest
class InventoryItemResponsesTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    Business testBusiness;
    User testUser;
    Address testAddressOne;
    Address testAddressTwo;
    Product testProduct;
    InventoryItem testInventoryItem;

    InventoryItemResponse testInventoryItemResponse;

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

        testInventoryItem = new InventoryItem(testProduct, 1, 1.0, 1.0,
                LocalDate.of(2021, 5, 10), LocalDate.of(2022, 11, 11),
                LocalDate.of(2022, 10, 11), LocalDate.of(2022, 12, 11));
        testInventoryItem = inventoryRepository.save(testInventoryItem);

        testInventoryItemResponse = new InventoryItemResponse();
        testInventoryItemResponse.setId(testInventoryItem.getId());
        testInventoryItemResponse.setProduct(ProductResponses.getResponse(testProduct));
        testInventoryItemResponse.setQuantity(1);
        testInventoryItemResponse.setPricePerItem(1.0);
        testInventoryItemResponse.setTotalPrice(1.0);
        testInventoryItemResponse.setManufactured(LocalDate.of(2021, 5, 10));
        testInventoryItemResponse.setSellBy(LocalDate.of(2022, 11, 11));
        testInventoryItemResponse.setBestBefore(LocalDate.of(2022, 10, 11));
        testInventoryItemResponse.setExpires(LocalDate.of(2022, 12, 11));
    }

    /**
     * Testing for getting inventory item response
     */
    @Test
    void getInventoryItemResponse() {
        InventoryItemResponse response = InventoryItemResponses.getResponse(testInventoryItem);

        Assertions.assertEquals(testInventoryItemResponse.getId(), response.getId());
        Assertions.assertEquals(testInventoryItemResponse.getProduct().getId(), response.getProduct().getId());
        Assertions.assertEquals(testInventoryItemResponse.getQuantity(), response.getQuantity());
        Assertions.assertEquals(testInventoryItemResponse.getPricePerItem(), response.getPricePerItem());
        Assertions.assertEquals(testInventoryItemResponse.getTotalPrice(), response.getTotalPrice());
        Assertions.assertEquals(testInventoryItemResponse.getManufactured(), response.getManufactured());
        Assertions.assertEquals(testInventoryItemResponse.getSellBy(), response.getSellBy());
        Assertions.assertEquals(testInventoryItemResponse.getBestBefore(), response.getBestBefore());
        Assertions.assertEquals(testInventoryItemResponse.getExpires(), response.getExpires());
    }

    /**
     * Testing for getting inventory item responses
     */
    @Test
    void getInventoryItemResponses() {
        List<InventoryItemResponse> check = new ArrayList<>();
        check.add(testInventoryItemResponse);

        List<InventoryItem> items = new ArrayList<>();
        items.add(testInventoryItem);

        List<InventoryItemResponse> responses = InventoryItemResponses.getAllResponses(items);

        Assertions.assertEquals(check.size(), responses.size());
        Assertions.assertEquals(check.get(0).getId(), responses.get(0).getId());
    }

}
