package org.seng302.main.responseFactoryTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.response.ListingResponse;
import org.seng302.main.dto.responsefactory.BusinessResponses;
import org.seng302.main.dto.responsefactory.InventoryItemResponses;
import org.seng302.main.dto.responsefactory.ListingResponses;
import org.seng302.main.models.*;
import org.seng302.main.repository.*;
import org.seng302.main.services.BusinessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Test listing responses
 */
@SpringBootTest
class ListingResponsesTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    ListingRepository listingRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    Business testBusiness;
    User testUser;
    Address testAddressOne;
    Address testAddressTwo;
    Product testProduct;
    InventoryItem testInventoryItem;
    Listing testListing;

    ListingResponse testListingResponse;

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

        testListing = new Listing(testInventoryItem, 1, 1.0, "more info",
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(5));
        testListing = listingRepository.save(testListing);

        testListingResponse = new ListingResponse();
        testListingResponse.setId(testListing.getId());
        testListingResponse.setQuantity(1);
        testListingResponse.setPrice(1.0);
        testListingResponse.setMoreInfo("more info");
        testListingResponse.setCreated(LocalDate.now().minusDays(5));
        testListingResponse.setCloses(LocalDate.now().plusDays(5));
        testListingResponse.setInventoryItem(InventoryItemResponses.getResponse(testInventoryItem));
        testListingResponse.setBusiness(BusinessResponses.getPartialResponse(testBusiness));
    }

    /**
     * Testing for getting listing response
     */
    @Test
    void testGettingListingResponse() {
        ListingResponse response = ListingResponses.getResponse(testListing, true);

        Assertions.assertEquals(testListingResponse.getId(), response.getId());
        Assertions.assertEquals(testListingResponse.getQuantity(), response.getQuantity());
        Assertions.assertEquals(testListingResponse.getPrice(), response.getPrice());
        Assertions.assertEquals(testListingResponse.getMoreInfo(), response.getMoreInfo());
        Assertions.assertEquals(testListingResponse.getCreated(), response.getCreated());
        Assertions.assertEquals(testListingResponse.getCloses(), response.getCloses());
        Assertions.assertEquals(testListingResponse.getInventoryItem().getId(), response.getInventoryItem().getId());
        Assertions.assertEquals(testListingResponse.getBusiness().getId(), response.getBusiness().getId());
    }

    /**
     * Testing for getting listing responses
     */
    @Test
    void testGettingListingResponses() {
        List<ListingResponse> check = new ArrayList<>();
        check.add(testListingResponse);

        List<Listing> listings = new ArrayList<>();
        listings.add(testListing);

        List<ListingResponse> responses = ListingResponses.getAllResponses(listings, true);

        Assertions.assertEquals(check.size(), responses.size());
        Assertions.assertEquals(check.get(0).getId(), responses.get(0).getId());
    }

}
