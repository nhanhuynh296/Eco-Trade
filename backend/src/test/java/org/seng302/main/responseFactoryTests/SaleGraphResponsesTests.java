package org.seng302.main.responseFactoryTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.response.SaleGraphResponse;
import org.seng302.main.dto.response.SaleGraphRevenueDatasetResponse;
import org.seng302.main.dto.response.SaleGraphSaleDatasetResponse;
import org.seng302.main.dto.response.SaleReportResponse;
import org.seng302.main.dto.responsefactory.SaleGraphResponses;
import org.seng302.main.models.*;
import org.seng302.main.repository.*;
import org.seng302.main.services.BusinessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

/**
 * Test sale graph responses methods.
 */
@SpringBootTest
class SaleGraphResponsesTests {

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

    @Autowired
    SaleRepository saleRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    Business testBusiness;
    User testUser;
    Address testAddressOne;
    Address testAddressTwo;
    Product testProduct;
    Sale testSale;
    List<SaleReportResponse> saleReports;

    SaleGraphResponse testSaleGraphResponse;

    /**
     * Set up the testing database entities
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

        testSale = new Sale(testBusiness.getId(), testProduct, LocalDate.of(2021, 5, 10), LocalDate.of(2021, 5, 10), 1, 1L, 1.0);
        testSale = saleRepository.save(testSale);

        SaleReportResponse testSaleReportResponse = new SaleReportResponse();
        testSaleReportResponse.setPeriod("Week 1");
        testSaleReportResponse.setInitialDate(LocalDate.of(2021, 5, 10));
        testSaleReportResponse.setFinalDate(LocalDate.of(2021, 5, 23));
        testSaleReportResponse.setTotalSales(1);
        testSaleReportResponse.setTotalRevenue(1.0);

        saleReports = List.of(testSaleReportResponse);

        testSaleGraphResponse = new SaleGraphResponse();
        testSaleGraphResponse.setLabels(List.of("Week 1"));

        SaleGraphSaleDatasetResponse saleDatasetResponse = new SaleGraphSaleDatasetResponse();
        saleDatasetResponse.setLabel("Sale Dataset");
        saleDatasetResponse.setBackgroundColor(List.of("#004b94"));
        saleDatasetResponse.setData(List.of(1));

        SaleGraphRevenueDatasetResponse revenueDatasetResponse = new SaleGraphRevenueDatasetResponse();
        revenueDatasetResponse.setLabel("Revenue Dataset");
        revenueDatasetResponse.setBackgroundColor(List.of("#890000"));
        revenueDatasetResponse.setData(List.of(1.0));

        testSaleGraphResponse.setDatasetSale(saleDatasetResponse);
        testSaleGraphResponse.setDatasetRevenue(revenueDatasetResponse);
    }

    /**
     * Tests getting the sale graph response from a list of sale report responses
     */
    @Test
    void testGettingSaleGraphResponse() {
        SaleGraphResponse response = SaleGraphResponses.getGraphResponse(saleReports);

        Assertions.assertEquals(testSaleGraphResponse.getLabels(), response.getLabels());
        Assertions.assertEquals(testSaleGraphResponse.getDatasetSale().getLabel(), response.getDatasetSale().getLabel());
        Assertions.assertEquals(testSaleGraphResponse.getDatasetSale().getBackgroundColor(), response.getDatasetSale().getBackgroundColor());
        Assertions.assertEquals(testSaleGraphResponse.getDatasetSale().getData(), response.getDatasetSale().getData());
        Assertions.assertEquals(testSaleGraphResponse.getDatasetRevenue().getLabel(), response.getDatasetRevenue().getLabel());
        Assertions.assertEquals(testSaleGraphResponse.getDatasetRevenue().getBackgroundColor(), response.getDatasetRevenue().getBackgroundColor());
        Assertions.assertEquals(testSaleGraphResponse.getDatasetRevenue().getData(), response.getDatasetRevenue().getData());
    }

}
