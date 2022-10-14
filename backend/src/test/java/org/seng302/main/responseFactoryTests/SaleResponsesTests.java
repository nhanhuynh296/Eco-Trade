package org.seng302.main.responseFactoryTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.response.SaleResponse;
import org.seng302.main.dto.responsefactory.BusinessResponses;
import org.seng302.main.dto.responsefactory.ProductResponses;
import org.seng302.main.dto.responsefactory.SaleResponses;
import org.seng302.main.models.*;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.ProductRepository;
import org.seng302.main.repository.SaleRepository;
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
class SaleResponsesTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    SaleRepository saleRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    Business testBusiness;
    User testUser;
    Address testAddressOne;
    Address testAddressTwo;
    Product testProduct;
    Sale testSale;

    SaleResponse testSaleResponse;

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

        testSale = new Sale(testBusiness.getId(), testProduct, LocalDate.now(), LocalDate.now(), 1, 1L, 1.0);
        testSale = saleRepository.save(testSale);

        testSaleResponse = new SaleResponse();
        testSaleResponse.setId(testSale.getSaleId());
        testSaleResponse.setBusinessId(testBusiness.getId());
        testSaleResponse.setProduct(ProductResponses.getResponse(testProduct));
        testSaleResponse.setSaleDate(LocalDate.now());
        testSaleResponse.setListingDate(LocalDate.now());
        testSaleResponse.setQuantity(1);
        testSaleResponse.setNumLikes(1L);
        testSaleResponse.setSoldFor(1.0);
        testSaleResponse.setBusiness(BusinessResponses.getPartialResponse(testBusiness));
    }

    /**
     * Testing for getting sale response
     */
    @Test
    void testGettingSaleResponse() {
        SaleResponse response = SaleResponses.getResponse(testSale);

        Assertions.assertEquals(testSaleResponse.getId(), response.getId());
        Assertions.assertEquals(testSaleResponse.getBusinessId(), response.getBusinessId());
        Assertions.assertEquals(testSaleResponse.getProduct().getId(), response.getProduct().getId());
        Assertions.assertEquals(testSaleResponse.getSaleDate(), response.getSaleDate());
        Assertions.assertEquals(testSaleResponse.getListingDate(), response.getListingDate());
        Assertions.assertEquals(testSaleResponse.getQuantity(), response.getQuantity());
        Assertions.assertEquals(testSaleResponse.getNumLikes(), response.getNumLikes());
        Assertions.assertEquals(testSaleResponse.getSoldFor(), response.getSoldFor());
        Assertions.assertEquals(testSaleResponse.getBusiness().getId(), response.getBusiness().getId());
    }

    /**
     * Testing for getting user responses
     */
    @Test
    void testGettingSaleResponses() {
        List<SaleResponse> check = new ArrayList<>();
        check.add(testSaleResponse);

        List<Sale> sales = new ArrayList<>();
        sales.add(testSale);

        List<SaleResponse> responses = SaleResponses.getAllResponses(sales);

        Assertions.assertEquals(check.size(), responses.size());
        Assertions.assertEquals(check.get(0).getId(), responses.get(0).getId());
    }

}
