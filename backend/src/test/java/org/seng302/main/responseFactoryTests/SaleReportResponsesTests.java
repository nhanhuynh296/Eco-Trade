package org.seng302.main.responseFactoryTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.response.SaleReportResponse;
import org.seng302.main.dto.responsefactory.SaleReportResponses;
import org.seng302.main.models.*;
import org.seng302.main.repository.*;
import org.seng302.main.services.BusinessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Test sale report responses methods.
 */
@SpringBootTest
class SaleReportResponsesTests {

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
    
    SaleReportResponse testSaleReportResponse;

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

        testSaleReportResponse = new SaleReportResponse();
        testSaleReportResponse.setPeriod("Week 1");
        testSaleReportResponse.setInitialDate(LocalDate.of(2021, 5, 10));
        testSaleReportResponse.setFinalDate(LocalDate.of(2021, 5, 23));
        testSaleReportResponse.setTotalSales(1);
        testSaleReportResponse.setTotalRevenue(1.0);
    }

    /**
     * Testing to get sale report response for a day
     */
    @Test
    void testGettingSaleReportResponseDay() {
        LocalDate endDate = LocalDate.of(2099, 1, 2);
        SaleReportResponse response = SaleReportResponses.getResponse(testSale.getSaleDate(), "DAY", 1);

        Assertions.assertEquals("Day 1", response.getPeriod());
        Assertions.assertEquals(LocalDate.of(2021, 5, 10), response.getInitialDate());
        Assertions.assertEquals(SaleReportResponses.getFinalDate(testSale.getSaleDate(), "DAY"), response.getFinalDate());
        Assertions.assertEquals(0, response.getTotalSales());
        Assertions.assertEquals(0.0, response.getTotalRevenue());
    }

    /**
     * Testing to get sale report response for a week
     */
    @Test
    void testGettingSaleReportResponseWeek() {
        LocalDate endDate = LocalDate.of(1984, 1, 2);
        SaleReportResponse response = SaleReportResponses.getResponse(testSale.getSaleDate(), "WEEK", 1);

        Assertions.assertEquals("Week 1", response.getPeriod());
        Assertions.assertEquals(LocalDate.of(2021, 5, 10), response.getInitialDate());
        Assertions.assertEquals(LocalDate.of(2021, 5, 16), response.getFinalDate());
        Assertions.assertEquals(0, response.getTotalSales());
        Assertions.assertEquals(0.0, response.getTotalRevenue());
    }

    /**
     * Testing to get sale report response for a month
     */
    @Test
    void testGettingSaleReportResponseMonth() {
        LocalDate endDate = LocalDate.of(1984, 1, 2);
        SaleReportResponse response = SaleReportResponses.getResponse(testSale.getSaleDate(), "MONTH", 1);

        Assertions.assertEquals("MAY", response.getPeriod());
        Assertions.assertEquals(LocalDate.of(2021, 5, 10), response.getInitialDate());
        Assertions.assertEquals(LocalDate.of(2021, 5, 31), response.getFinalDate());
        Assertions.assertEquals(0, response.getTotalSales());
        Assertions.assertEquals(0.0, response.getTotalRevenue());
    }

    /**
     * Testing to get sale report response for a year
     */
    @Test
    void testGettingSaleReportResponseYear() {
        LocalDate endDate = LocalDate.of(1984, 1, 2);
        SaleReportResponse response = SaleReportResponses.getResponse(testSale.getSaleDate(), "YEAR", 1);

        Assertions.assertEquals("2021", response.getPeriod());
        Assertions.assertEquals(LocalDate.of(2021, 5, 10), response.getInitialDate());
        Assertions.assertEquals(LocalDate.of(2021, 12, 31), response.getFinalDate());
        Assertions.assertEquals(0, response.getTotalSales());
        Assertions.assertEquals(0.0, response.getTotalRevenue());
    }

    /**
     * Tests that the correct rows are returned by SaleReportResponses.getEmptyResponses for days
     */
    @Test
    void testGettingSaleReportEmptyResponsesDay() {
        LocalDate now = LocalDate.of(2021, 9, 29);
        LocalDate date = testSale.getSaleDate().minusYears(10);
        LocalDate endDate = now.plusYears(1);

        Map<LocalDate, SaleReportResponse> check = new HashMap<>();
        check.put(date, SaleReportResponses.getResponse(date, "DAY", 1));
        check.put(date.plusDays(1), SaleReportResponses.getResponse(date.plusDays(1), "DAY", 2));
        check.put(date.plusDays(2), SaleReportResponses.getResponse(date.plusDays(2), "DAY", 3));

        Map<LocalDate, SaleReportResponse> responses = SaleReportResponses.getEmptyResponses("DAY", date, endDate);

        Assertions.assertTrue(Math.abs(4160 - responses.size()) < 2);
        Assertions.assertEquals(check.get(date).getPeriod(), responses.get(date).getPeriod());
        Assertions.assertEquals(check.get(date).getInitialDate(), responses.get(date).getInitialDate());
        Assertions.assertEquals(check.get(date).getFinalDate(), responses.get(date).getFinalDate());
        Assertions.assertEquals(0, responses.get(date).getTotalSales());
        Assertions.assertEquals(0.0, responses.get(date).getTotalRevenue());
    }

    /**
     * Tests that the correct rows are returned by SaleReportResponses.getEmptyResponses for weeks
     */
    @Test
    void testGettingSaleReportEmptyResponsesWeek() {
        LocalDate date = SaleReportResponses.getInitialDate(LocalDate.of(2021, 5, 10), "WEEK");
        LocalDate startDate = date.plusDays(3);
        LocalDate endDate = date.plusWeeks(3);

        Map<LocalDate, SaleReportResponse> check = new HashMap<>();
        check.put(date, SaleReportResponses.getResponse(date, "WEEK", 1));
        check.put(date.plusWeeks(1), SaleReportResponses.getResponse(date.plusWeeks(1), "WEEK", 2));
        check.put(date.plusWeeks(2), SaleReportResponses.getResponse(date.plusWeeks(2), "WEEK", 3));
        check.put(date.plusWeeks(3), SaleReportResponses.getResponse(date.plusWeeks(3), "WEEK", 4));

        Map<LocalDate, SaleReportResponse> responses = SaleReportResponses.getEmptyResponses("WEEK", startDate, endDate);

        Assertions.assertEquals(check.size(), responses.size());
        Assertions.assertEquals(check.get(date).getPeriod(), responses.get(startDate).getPeriod());
        Assertions.assertEquals(startDate, responses.get(startDate).getInitialDate());
        Assertions.assertEquals(check.get(date).getFinalDate(), responses.get(startDate).getFinalDate());
        Assertions.assertEquals(0, responses.get(startDate).getTotalSales());
        Assertions.assertEquals(0.0, responses.get(startDate).getTotalRevenue());
    }

    /**
     * Tests that the correct rows are returned by SaleReportResponses.getEmptyResponses for months
     */
    @Test
    void testGettingSaleReportEmptyResponsesMonth() {
        LocalDate date = SaleReportResponses.getInitialDate(LocalDate.of(2021, 5, 10), "MONTH");
        LocalDate endDate = date.plusMonths(4);

        Map<LocalDate, SaleReportResponse> check = new HashMap<>();
        check.put(date, SaleReportResponses.getResponse(date, "MONTH", 1));
        check.put(date.plusMonths(1), SaleReportResponses.getResponse(date.plusMonths(1), "MONTH", 2));
        check.put(date.plusMonths(2), SaleReportResponses.getResponse(date.plusMonths(2), "MONTH", 3));
        check.put(date.plusMonths(3), SaleReportResponses.getResponse(date.plusMonths(3), "MONTH", 4));
        check.put(date.plusMonths(4), SaleReportResponses.getResponse(date.plusMonths(4), "MONTH", 5));

        Map<LocalDate, SaleReportResponse> responses = SaleReportResponses.getEmptyResponses("MONTH", date, endDate);

        LocalDate keyOfMonthFive = SaleReportResponses.getInitialDate(date.plusMonths(4), "MONTH");

        Assertions.assertEquals(check.size(), responses.size());
        Assertions.assertEquals(check.get(date.plusMonths(4)).getPeriod(), responses.get(keyOfMonthFive).getPeriod());
        Assertions.assertEquals(SaleReportResponses.getInitialDate(date.plusMonths(4), "MONTH"),
                responses.get(keyOfMonthFive).getInitialDate());
        Assertions.assertEquals(check.get(date.plusMonths(4)).getFinalDate(), responses.get(keyOfMonthFive).getFinalDate());
        Assertions.assertEquals(0, responses.get(keyOfMonthFive).getTotalSales());
        Assertions.assertEquals(0.0, responses.get(keyOfMonthFive).getTotalRevenue());
    }

    /**
     * Tests that the correct rows are returned by SaleReportResponses.getResponses for years.
     */
    @Test
    void testGettingSaleReportEmptyResponsesYear() {
        LocalDate date = LocalDate.of(2021, 5, 10);
        LocalDate endDate = date.plusYears(5);

        Map<LocalDate, SaleReportResponse> check = new HashMap<>();
        check.put(date, SaleReportResponses.getResponse(date, "YEAR", 1));
        check.put(date.plusYears(1), SaleReportResponses.getResponse(date.plusYears(1), "YEAR", 2));
        check.put(date.plusYears(2), SaleReportResponses.getResponse(date.plusYears(2), "YEAR", 3));
        check.put(date.plusYears(3), SaleReportResponses.getResponse(date.plusYears(3), "YEAR", 4));
        check.put(date.plusYears(4), SaleReportResponses.getResponse(date.plusYears(4), "YEAR", 5));

        LocalDate keyOfYearSix = SaleReportResponses.getInitialDate(date.plusYears(5), "YEAR");
        check.put(keyOfYearSix, SaleReportResponses.getResponse(keyOfYearSix, "YEAR", 6));

        Map<LocalDate, SaleReportResponse> responses = SaleReportResponses.getEmptyResponses("YEAR", date, endDate);

        Assertions.assertEquals(check.size(), responses.size());
        Assertions.assertEquals(check.get(keyOfYearSix).getPeriod(), responses.get(keyOfYearSix).getPeriod());
        Assertions.assertEquals(check.get(keyOfYearSix).getInitialDate(), responses.get(keyOfYearSix).getInitialDate());
        Assertions.assertEquals(check.get(keyOfYearSix).getFinalDate(), responses.get(keyOfYearSix).getFinalDate());
        Assertions.assertEquals(0, responses.get(keyOfYearSix).getTotalSales());
        Assertions.assertEquals(0.0, responses.get(keyOfYearSix).getTotalRevenue());
    }

    /**
     * Tests for updating sale report response for WEEK
     */
    @Test
    void testUpdatingSaleReportResponseWeek() {
        SaleReportResponse response = SaleReportResponses.getResponse(testSale.getSaleDate(), "WEEK", 1);
        SaleReportResponse updatedResponse = SaleReportResponses.updateResponse(testSale, response);

        Assertions.assertEquals(testSaleReportResponse.getPeriod(), updatedResponse.getPeriod());
        Assertions.assertEquals(testSaleReportResponse.getInitialDate(), updatedResponse.getInitialDate());
        Assertions.assertEquals(LocalDate.of(2021, 5, 16), updatedResponse.getFinalDate());
        Assertions.assertEquals(testSaleReportResponse.getTotalSales(), updatedResponse.getTotalSales());
        Assertions.assertEquals(testSaleReportResponse.getTotalRevenue(), updatedResponse.getTotalRevenue());
    }

    /**
     * Tests for updating sale report response for YEAR
     */
    @Test
    void testUpdatingSaleReportResponseYear() {
        LocalDate expectedFinalDate = LocalDate.of(1984, 7, 20);
        SaleReportResponse response = SaleReportResponses.getResponse(testSale.getSaleDate(), "YEAR", 1);
        SaleReportResponse updatedResponse = SaleReportResponses.updateResponse(testSale, response);

        testSaleReportResponse.setPeriod("2021");

        Assertions.assertEquals(testSaleReportResponse.getPeriod(), updatedResponse.getPeriod());
        Assertions.assertEquals(testSaleReportResponse.getInitialDate(), updatedResponse.getInitialDate());
        Assertions.assertEquals(LocalDate.of(2021, 12, 31), updatedResponse.getFinalDate());
        Assertions.assertEquals(testSaleReportResponse.getTotalSales(), updatedResponse.getTotalSales());
        Assertions.assertEquals(testSaleReportResponse.getTotalRevenue(), updatedResponse.getTotalRevenue());
    }

    /**
     * Checks that the switch for getting the initial date depending on granularity returns the same day when
     * granulating by DAY
     */
    @Test
    void testGetInitialDateForDay() {
        LocalDate inputDate = LocalDate.ofYearDay(2021, 244);
        LocalDate initialDate = SaleReportResponses.getInitialDate(inputDate, "DAY");
        Assertions.assertEquals(inputDate, initialDate);
    }

    /**
     * Checks that the switch for getting the initial date depending on granularity returns the first day of the week
     * granulating by WEEK
     */
    @Test
    void testGetInitialDateForWeekDay255() {
        LocalDate inputDate = LocalDate.ofYearDay(2021, 255);
        LocalDate initialDate = SaleReportResponses.getInitialDate(LocalDate.ofYearDay(2021, 255), "WEEK");
        Assertions.assertEquals(inputDate.minusDays(6), initialDate);
    }

    /**
     * Checks that the switch for getting the initial date depending on granularity returns the first day of the week
     * granulating by WEEK
     */
    @Test
    void testGetInitialDateForWeekDay258() {
        LocalDate inputDate = LocalDate.ofYearDay(2021, 258);
        LocalDate initialDate = SaleReportResponses.getInitialDate(inputDate, "WEEK");
        Assertions.assertEquals(inputDate.minusDays(2), initialDate);
    }

    /**
     * Checks that the switch for getting the initial date depending on granularity returns the first day of the month
     * granulating by MONTH
     */
    @Test
    void testGetInitialDateForFifthMonth() {
        LocalDate inputDate = LocalDate.ofYearDay(2021, 132);
        LocalDate initialDate = SaleReportResponses.getInitialDate(inputDate, "MONTH");
        Assertions.assertEquals(inputDate.minusDays(11), initialDate);
    }

    /**
     * Checks that the switch for getting the initial date depending on granularity returns the first day of the month
     * granulating by MONTH
     */
    @Test
    void testGetInitialDateForFirstMonth() {
        LocalDate inputDate = LocalDate.ofYearDay(2021, 1);
        LocalDate initialDate = SaleReportResponses.getInitialDate(inputDate, "MONTH");
        Assertions.assertEquals(inputDate, initialDate);
    }

    /**
     * Checks that the switch for getting the initial date depending on granularity returns the first day of the year
     * granulating by YEAR
     */
    @Test
    void testGetInitialDateForYear2021() {
        LocalDate inputDate = LocalDate.ofYearDay(2021, 4);
        LocalDate initialDate = SaleReportResponses.getInitialDate(inputDate, "YEAR");
        Assertions.assertEquals(initialDate, inputDate.minusDays(3));
    }

    /**
     * Checks that the switch for getting the initial date depending on granularity returns the first day of the year
     * granulating by YEAR
     */
    @Test
    void testGetInitialDateForYear2013() {
        LocalDate inputDate = LocalDate.ofYearDay(2013, 145);
        LocalDate initialDate = SaleReportResponses.getInitialDate(inputDate, "YEAR");
        Assertions.assertEquals(initialDate, inputDate.minusDays(144));
    }

    /**
     * Checks that the switch for getting the final date depending on granularity returns the same day when
     * granulating by DAY
     */
    @Test
    void testGetFinalDateForDay() {
        LocalDate inputDate = LocalDate.ofYearDay(2021, 244);
        LocalDate finalDate = SaleReportResponses.getFinalDate(inputDate, "DAY");
        Assertions.assertEquals(inputDate, finalDate);
    }

    /**
     * Checks that the switch for getting the final date depending on granularity returns the last day of the week
     * granulating by WEEK
     */
    @Test
    void testGetFinalDateForWeekDay255() {
        LocalDate inputDate = LocalDate.of(2021, 9, 12);
        LocalDate finalDate = SaleReportResponses.getFinalDate(inputDate, "WEEK");
        Assertions.assertEquals(inputDate, finalDate);
    }

    /**
     * Checks that the switch for getting the final date depending on granularity returns the last day of the week
     * granulating by WEEK
     */
    @Test
    void testGetFinalDateForWeekDay258() {
        LocalDate inputDate = LocalDate.of(2021, 9, 15);
        LocalDate finalDate = SaleReportResponses.getFinalDate(inputDate, "WEEK");
        Assertions.assertEquals(LocalDate.of(2021, 9, 19), finalDate);
    }

    /**
     * Checks that the switch for getting the final date depending on granularity returns the last day of the month
     * granulating by MONTH
     */
    @Test
    void testGetFinalDateForFifthMonth() {
        LocalDate inputDate = LocalDate.ofYearDay(2021, 132);
        LocalDate finalDate = SaleReportResponses.getFinalDate(inputDate, "MONTH");
        Assertions.assertEquals(inputDate.plusDays(19), finalDate);
    }

    /**
     * Checks that the switch for getting the final date depending on granularity returns the last day of the month
     * granulating by MONTH
     */
    @Test
    void testGetFinalDateForFirstMonth() {
        LocalDate inputDate = LocalDate.ofYearDay(2021, 31);
        LocalDate finalDate = SaleReportResponses.getFinalDate(inputDate, "MONTH");
        Assertions.assertEquals(inputDate, finalDate);
    }

    /**
     * Checks that the switch for getting the final date depending on granularity returns the last day of the year
     * granulating by YEAR
     */
    @Test
    void testGetFinalDateForYear2021() {
        LocalDate inputDate = LocalDate.ofYearDay(2021, 365);
        LocalDate finalDate = SaleReportResponses.getFinalDate(inputDate, "YEAR");
        Assertions.assertEquals(finalDate, inputDate);
    }

    /**
     * Checks that the switch for getting the final date depending on granularity returns the last day of the year
     * granulating by YEAR
     */
    @Test
    void testGetFinalDateForYear2013() {
        LocalDate inputDate = LocalDate.ofYearDay(2013, 145);
        LocalDate finalDate = SaleReportResponses.getFinalDate(inputDate, "YEAR");
        Assertions.assertEquals(finalDate, inputDate.plusDays(220));
    }

    /**
     * Testing for getting final date
     */
    @Test
    void testGettingFinalDate() {
        LocalDate finalDate = LocalDate.of(2021, 9, 10);
        LocalDate resultDay = SaleReportResponses.getFinalDate(finalDate, "DAY");
        LocalDate resultWeek = SaleReportResponses.getFinalDate(finalDate, "WEEK");
        LocalDate resultMonth = SaleReportResponses.getFinalDate(finalDate, "MONTH");
        LocalDate resultYear = SaleReportResponses.getFinalDate(finalDate, "YEAR");

        LocalDate checkDay = LocalDate.of(2021, 9, 10);
        LocalDate checkWeek = LocalDate.of(2021, 9, 12);
        LocalDate checkMonth = LocalDate.of(2021, 9, 30);
        LocalDate checkYear = LocalDate.of(2021, 12, 31);

        Assertions.assertEquals(checkDay, resultDay);
        Assertions.assertEquals(checkWeek, resultWeek);
        Assertions.assertEquals(checkMonth, resultMonth);
        Assertions.assertEquals(checkYear, resultYear);
    }

}
