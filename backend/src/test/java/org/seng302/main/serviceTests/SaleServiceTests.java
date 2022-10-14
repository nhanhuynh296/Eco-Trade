package org.seng302.main.serviceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.response.*;
import org.seng302.main.dto.responsefactory.SaleReportResponses;
import org.seng302.main.models.*;
import org.seng302.main.repository.*;
import org.seng302.main.services.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Tests the sale services and the granulated sales report.
 */
@SpringBootTest
class SaleServiceTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private SaleService saleService;

    @Autowired
    private SaleRepository saleRepository;

    private User userOne;
    private User userTwo;
    private Listing testListing;
    private Business testBusinessOne;
    private Business testBusinessThree;
    private Sale soldListingOld;
    private Sale soldListingNewOne;
    private Sale soldListingNewTwo;
    private Sale soldListingNewThree;
    private Sale soldListingNewThreeDuplicate;
    private Sale soldListingDifferentBusiness;
    private Sale soldListingDifferentBusinessAndUser;
    private SaleGraphResponse testSaleGraphResponse;

    /**
     * Set up the testing database entities
     */
    @BeforeEach
    public void init() {
        Address addressUserOne = new Address("1", "Nowhere", "Varrock", "Tirannwn", "Gilenor", "800");
        userOne = new User("A", "A", "A", "A", "A", "cja128@uclive.ac.nz",
                LocalDate.now(), "", addressUserOne, LocalDate.now(), "ROLE_USER", "password123", "55555", null);
        userOne = userRepository.save(userOne);

        Address addressUserTwo = new Address("2", "Test", "test", "test", "test", "980");
        userTwo = new User("A", "A", "A", "A", "A", "cja128@uclive.ac.nz",
                LocalDate.now(), "", addressUserTwo, LocalDate.now(), "ROLE_USER", "password123", "55555", null);
        userTwo = userRepository.save(userTwo);

        Address addressOne = new Address("5", "Somewhere", "Over", "The", "Rainbow", "800");
        Address addressTwo = new Address("6", "SomewhereElse", "Over", "The", "Valley", "8090");
        Address addressThree = new Address("4", "SomewhereElse", "Over", "The", "Valley", "8090");
        Address addressFour = new Address("35", "SomewhereElse", "Over", "The", "Valley", "8091");

        testBusinessOne = new Business(userOne.getId(), "Lumbridge General Store1", "Description1", addressOne,
                "Accomodation", LocalDate.now());
        testBusinessOne = businessRepository.save(testBusinessOne);

        Business testBusinessTwo = new Business(userOne.getId(), "Lumbridge General Store2", "Description1", addressTwo,
                "Accomodation", LocalDate.now());
        testBusinessTwo = businessRepository.save(testBusinessTwo);

        testBusinessThree = new Business(userTwo.getId(), "Lumbridge General Store3", "Description1", addressThree,
                "Accomodation", LocalDate.now());
        testBusinessThree = businessRepository.save(testBusinessThree);

        Business testBusinessFour = new Business(userTwo.getId(), "Lumbridge General Store3", "Description1", addressFour,
                "Accomodation", LocalDate.now());
        testBusinessFour = businessRepository.save(testBusinessFour);

        Product testProduct = new Product(testBusinessOne.getId(), testBusinessOne, "Watties Baked Beans", "Baked beans in a can",
                "Heinz Wattie's Limited", 2.2, LocalDate.now());
        testProduct = productRepository.save(testProduct);

        Product testProductTwo = new Product(testBusinessTwo.getId(), testBusinessTwo, "Watties Baked Beans", "Baked beans in a can",
                "Heinz Wattie's Limited", 2.2, LocalDate.now());
        testProductTwo = productRepository.save(testProductTwo);

        InventoryItem testInventory = new InventoryItem(testProduct, 69, 10.0, 100.0,
                LocalDate.now(), null, null, LocalDate.now().plusDays(20));
        testInventory = inventoryRepository.save(testInventory);

        testListing = new Listing(testInventory, 2, 4.0, "Beans",
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(5));
        testListing = listingRepository.save(testListing);

        soldListingOld = new Sale(testBusinessOne.getId(), testProduct,
                LocalDate.of(1997, 5, 10),
                LocalDate.of(1997, 5, 10),
                100, 200L, 20.5);
        soldListingOld = saleRepository.save(soldListingOld);

        soldListingNewOne = new Sale(testBusinessOne.getId(), testProduct,
                LocalDate.of(2020, 2, 2),
                LocalDate.of(2020, 2, 2),
                951, 49L, 56.0);
        soldListingNewOne = saleRepository.save(soldListingNewOne);

        soldListingNewTwo = new Sale(testBusinessOne.getId(), testProductTwo,
                LocalDate.of(2021, 5, 10),
                LocalDate.of(2021, 5, 10),
                4500, 43L, 200.0);
        soldListingNewTwo = saleRepository.save(soldListingNewTwo);

        soldListingNewThree = new Sale(testBusinessOne.getId(), testProduct,
                LocalDate.of(2022, 2, 28),
                LocalDate.of(2022, 11, 3),
                770, 2L, 51.0);
        soldListingNewThree = saleRepository.save(soldListingNewThree);

        soldListingNewThreeDuplicate = new Sale(testBusinessOne.getId(), testProduct,
                LocalDate.of(2022, 2, 28),
                LocalDate.of(2022, 11, 3),
                770, 2L, 51.0);
        soldListingNewThreeDuplicate = saleRepository.save(soldListingNewThreeDuplicate);

        soldListingDifferentBusiness = new Sale(testBusinessTwo.getId(), testProductTwo,
                LocalDate.of(2084, 2, 28),
                LocalDate.of(2084, 11, 3),
                770, 2L, 32.0);
        soldListingDifferentBusiness = saleRepository.save(soldListingDifferentBusiness);

        soldListingDifferentBusinessAndUser = new Sale(testBusinessFour.getId(), testProductTwo,
                LocalDate.of(2084, 2, 28),
                LocalDate.of(2084, 11, 3),
                770, 2L, 32.0);
        soldListingDifferentBusinessAndUser = saleRepository.save(soldListingDifferentBusinessAndUser);

        testSaleGraphResponse = new SaleGraphResponse();
        testSaleGraphResponse.setLabels(List.of("2022"));

        SaleGraphSaleDatasetResponse saleDatasetResponse = new SaleGraphSaleDatasetResponse();
        saleDatasetResponse.setLabel("Sale Dataset");
        saleDatasetResponse.setBackgroundColor(List.of("#004b94"));
        saleDatasetResponse.setData(List.of(1540));

        SaleGraphRevenueDatasetResponse revenueDatasetResponse = new SaleGraphRevenueDatasetResponse();
        revenueDatasetResponse.setLabel("Revenue Dataset");
        revenueDatasetResponse.setBackgroundColor(List.of("#890000"));
        revenueDatasetResponse.setData(List.of(102.0));

        testSaleGraphResponse.setDatasetSale(saleDatasetResponse);
        testSaleGraphResponse.setDatasetRevenue(revenueDatasetResponse);
    }

    /**
     * Test that a listing's values are correctly transferred to a sale record
     * using saleService.createSaleFromListing
     */
    @Test
    @Transactional
    void testGetSaleFromListing() {
        Sale sale = saleService.createSaleFromListing(testListing.getId());
        Assertions.assertEquals(testListing.getInventoryItem().getProduct().getBusinessId(), sale.getBusinessId());
        Assertions.assertEquals(testListing.getInventoryItem().getProduct().toString(), sale.getProduct().toString());
        Assertions.assertEquals(LocalDate.now(), sale.getSaleDate());
        Assertions.assertEquals(testListing.getCreated(), sale.getListingDate());
        Assertions.assertEquals(testListing.getQuantity(), sale.getQuantity());
        Assertions.assertEquals(testListing.getTotalLikes(), sale.getNumLikes());
        Assertions.assertEquals(testListing.getPrice(), sale.getSoldFor());
    }

    /**
     * Checks that the method for combining sold listing information by days is accurate.
     */
    @Test
    void testGranulationByDays() {
        List<Sale> salesList = new ArrayList<>();
        salesList.add(soldListingOld);

        LocalDate startDate = soldListingOld.getSaleDate();
        LocalDate endDate = soldListingOld.getSaleDate().plusDays(2);

        LocalDate hashKey = SaleReportResponses.getInitialDate(soldListingOld.getSaleDate(), "DAY");

        Map<LocalDate, SaleReportResponse> map = saleService.granulateSalesHistory(salesList, "DAY",
                startDate, endDate);

        Assertions.assertEquals(3, map.size());
        Assertions.assertEquals(soldListingOld.getSoldFor(), map.get(hashKey).getTotalRevenue());
    }

    /**
     * Checks that the method for combining sold listing information by weeks is accurate.
     */
    @Test
    void testGranulationByWeeks() {
        List<Sale> salesList = new ArrayList<>();
        salesList.add(soldListingOld);

        LocalDate startDate = soldListingOld.getSaleDate();
        LocalDate endDate = soldListingOld.getSaleDate().plusWeeks(6);

        Map<LocalDate, SaleReportResponse> map = saleService.granulateSalesHistory(salesList, "WEEK",
                startDate, endDate);

        Assertions.assertEquals(7, map.size());
        Assertions.assertEquals(soldListingOld.getSoldFor(), map.get(startDate).getTotalRevenue());
        Assertions.assertEquals(soldListingOld.getSaleDate().plusDays(1), map.get(startDate).getFinalDate());
        Assertions.assertEquals(startDate, map.get(startDate).getInitialDate());
    }

    /**
     * Checks that the method for combining sold listing information by weeks is accurate.
     */
    @Test
    void testGranulationByMonths() {
        List<Sale> salesList = new ArrayList<>();
        salesList.add(soldListingOld);
        salesList.add(soldListingOld);
        salesList.add(soldListingOld);
        salesList.add(soldListingNewTwo);

        LocalDate startDate = soldListingOld.getSaleDate();
        LocalDate endDate = soldListingNewTwo.getSaleDate().plusMonths(1);

        LocalDate hashKeyOfOld = soldListingOld.getSaleDate();
        LocalDate hashKeyOfNew = SaleReportResponses.getInitialDate(soldListingNewTwo.getSaleDate(), "MONTH");

        Map<LocalDate, SaleReportResponse> map = saleService.granulateSalesHistory(salesList, "MONTH",
                startDate, endDate);

        Assertions.assertEquals(290, map.size());
        Assertions.assertEquals(soldListingOld.getSoldFor() * 3, map.get(hashKeyOfOld).getTotalRevenue());
        Assertions.assertEquals(soldListingNewTwo.getSoldFor(), map.get(hashKeyOfNew).getTotalRevenue());
    }

    /**
     * Checks that the method for combining sold listing information by weeks is accurate.
     */
    @Test
    void testGranulationByYears() {
        List<Sale> salesList = new ArrayList<>();
        salesList.add(soldListingOld);
        salesList.add(soldListingOld);
        salesList.add(soldListingOld);
        salesList.add(soldListingNewTwo);
        salesList.add(soldListingNewTwo);

        LocalDate startDate = soldListingOld.getSaleDate().minusYears(2);
        LocalDate endDate = soldListingNewTwo.getSaleDate().plusYears(10);

        LocalDate hashKeyOfOld = SaleReportResponses.getInitialDate(soldListingOld.getSaleDate(), "YEAR");
        LocalDate hashKeyOfNew = SaleReportResponses.getInitialDate(soldListingNewTwo.getSaleDate(), "YEAR");

        LocalDate expectedEndDate = SaleReportResponses.getFinalDate(soldListingNewTwo.getSaleDate(), "YEAR");

        Map<LocalDate, SaleReportResponse> map = saleService.granulateSalesHistory(salesList, "YEAR",
                startDate, endDate);

        Assertions.assertEquals(37, map.size());
        Assertions.assertEquals(soldListingOld.getSoldFor() * 3, map.get(hashKeyOfOld).getTotalRevenue());
        Assertions.assertEquals(hashKeyOfOld, map.get(hashKeyOfOld).getInitialDate());
        Assertions.assertEquals(soldListingNewTwo.getSoldFor() * 2, map.get(hashKeyOfNew).getTotalRevenue());
        Assertions.assertEquals(expectedEndDate, map.get(hashKeyOfNew).getFinalDate());
    }

    /**
     * This tests that the right number of blank rows are returned when there
     * are no sales for a business.
     */
    @Test
    void testGranulateSalesHistoryNoSales() {
        List<Sale> salesList = new ArrayList<>();

        LocalDate defaultDate = LocalDate.of(1997, 5, 10);

        Map<LocalDate, SaleReportResponse> map = saleService.granulateSalesHistory(salesList, "WEEK",
                defaultDate, defaultDate);

        Assertions.assertEquals(1, map.size());

        Map.Entry<LocalDate,SaleReportResponse> entry = map.entrySet().iterator().next();
        Assertions.assertEquals(0.0, entry.getValue().getTotalRevenue());
        Assertions.assertEquals(0, entry.getValue().getTotalSales());
    }

    /**
     * This tests that for a period of 100 years the right amount of information is returned.
     */
    @Test
    void testGranulateSalesHistoryHugeCustomDateDifference() {
        List<Sale> salesList = new ArrayList<>();
        salesList.add(soldListingOld);
        salesList.add(soldListingOld);
        salesList.add(soldListingOld);
        salesList.add(soldListingOld);
        salesList.add(soldListingNewTwo);
        salesList.add(soldListingNewThree);
        salesList.add(soldListingNewThree);

        LocalDate defaultDate = soldListingOld.getSaleDate();

        LocalDate startDate = defaultDate.minusYears(100);
        LocalDate endDate = defaultDate.plusYears(100);

        LocalDate hashKeyOfOld = SaleReportResponses.getInitialDate(soldListingOld.getSaleDate(), "DAY");
        LocalDate hashKeyOfNewTwo = SaleReportResponses.getInitialDate(soldListingNewThree.getSaleDate(), "DAY");

        Map<LocalDate, SaleReportResponse> map = saleService.granulateSalesHistory(salesList, "DAY",
                startDate, endDate);

        Assertions.assertEquals(73050, map.size());
        Assertions.assertEquals(soldListingOld.getSoldFor() * 4, map.get(hashKeyOfOld).getTotalRevenue());
        Assertions.assertEquals(soldListingOld.getQuantity() * 4, map.get(hashKeyOfOld).getTotalSales());
        Assertions.assertEquals(hashKeyOfOld, map.get(hashKeyOfOld).getInitialDate());
        Assertions.assertEquals(soldListingNewThree.getSoldFor() * 2, map.get(hashKeyOfNewTwo).getTotalRevenue());
        Assertions.assertEquals(soldListingNewThree.getQuantity() * 2, map.get(hashKeyOfNewTwo).getTotalSales());
        Assertions.assertEquals(hashKeyOfNewTwo, map.get(hashKeyOfNewTwo).getInitialDate());
    }

    /**
     * Checks that when given the same date at least one row is returned
     */
    @Test
    void testGranulationDuplicateStartEndDates() {
        List<Sale> salesList = new ArrayList<>();

        LocalDate startDate = soldListingOld.getSaleDate();
        LocalDate endDate = soldListingOld.getSaleDate();

        Map<LocalDate, SaleReportResponse> map = saleService.granulateSalesHistory(salesList, "DAY",
                startDate, endDate);

        Assertions.assertEquals(1, map.size());
    }

    /**
     * Checks that the returnPaginatedOrderedList method returns a correctly ordered array of sales report rows
     * for day granularity
     */
    @Test
    void testOrderingForDays() {
        List<Sale> salesList = new ArrayList<>();
        salesList.add(soldListingOld);

        LocalDate startDate = soldListingOld.getSaleDate();
        LocalDate endDate = soldListingOld.getSaleDate().plusDays(3);

        Map<LocalDate, SaleReportResponse> map = saleService.granulateSalesHistory(salesList, "DAY",
                startDate, endDate);

        // Add one because there is an extra day not included by this calculation
        int expectedTotalRows = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        List<SaleReportResponse> orderedList = saleService.returnOrderedList(map, endDate);

        Assertions.assertEquals(expectedTotalRows, orderedList.size());

        Assertions.assertEquals(soldListingOld.getSaleDate(), orderedList.get(0).getInitialDate());
        Assertions.assertEquals(soldListingOld.getSaleDate(), orderedList.get(0).getFinalDate());

        Assertions.assertEquals(soldListingOld.getSaleDate().plusDays(1), orderedList.get(1).getInitialDate());
        Assertions.assertEquals(soldListingOld.getSaleDate().plusDays(1), orderedList.get(1).getFinalDate());

        Assertions.assertEquals(soldListingOld.getSaleDate().plusDays(2), orderedList.get(2).getInitialDate());
        Assertions.assertEquals(soldListingOld.getSaleDate().plusDays(2), orderedList.get(2).getFinalDate());

        Assertions.assertEquals(soldListingOld.getSaleDate().plusDays(3), orderedList.get(3).getInitialDate());
        Assertions.assertEquals(soldListingOld.getSaleDate().plusDays(3), orderedList.get(3).getFinalDate());
    }

    /**
     * Checks that the returnPaginatedOrderedList method returns a correctly ordered array of sales report rows
     * for week granularity
     */
    @Test
    void testOrderingForWeeks() {
        List<Sale> salesList = new ArrayList<>();
        salesList.add(soldListingNewOne);
        salesList.add(soldListingOld);
        salesList.add(soldListingNewThree);
        salesList.add(soldListingNewTwo);
        salesList.add(soldListingNewThreeDuplicate);

        LocalDate startDate = soldListingOld.getSaleDate().minusYears(1);
        LocalDate endDate = soldListingNewThreeDuplicate.getSaleDate();

        Map<LocalDate, SaleReportResponse> map = saleService.granulateSalesHistory(salesList, "WEEK",
                startDate, endDate);

        // Add two because there are two weeks on either side of this calculation which are not included
        List<SaleReportResponse> orderedList = saleService.returnOrderedList(map, endDate);

        int expectedTotalRows = 1348;
        Assertions.assertEquals(expectedTotalRows, orderedList.size());

        Assertions.assertEquals(startDate, orderedList.get(0).getInitialDate());
        Assertions.assertEquals(startDate.plusDays(2), orderedList.get(0).getFinalDate());

        Assertions.assertEquals(LocalDate.of(2022, 2, 21), orderedList.get(expectedTotalRows-2).getInitialDate());
        Assertions.assertEquals(LocalDate.of(2022, 2, 27), orderedList.get(expectedTotalRows-2).getFinalDate());

        Assertions.assertEquals(soldListingNewThreeDuplicate.getSaleDate(), orderedList.get(expectedTotalRows-1).getInitialDate());
        Assertions.assertEquals(endDate, orderedList.get(expectedTotalRows-1).getFinalDate());
    }

    /**
     * Checks that the returnPaginatedOrderedList method returns a correctly ordered array of sales report rows
     * for month granularity
     */
    @Test
    void testOrderingForMonth() {
        List<Sale> salesList = new ArrayList<>();
        salesList.add(soldListingOld);
        salesList.add(soldListingNewTwo);

        LocalDate startDate = soldListingOld.getSaleDate();
        LocalDate endDate = soldListingNewTwo.getSaleDate().plusMonths(3);

        Map<LocalDate, SaleReportResponse> map = saleService.granulateSalesHistory(salesList, "MONTH",
                startDate, endDate);

        // Add one because there is an extra month not included by this calculation
        int expectedTotalRows = (int) ChronoUnit.MONTHS.between(startDate, endDate) + 1;
        List<SaleReportResponse> orderedList = saleService.returnOrderedList(map, endDate);

        Assertions.assertEquals(expectedTotalRows, orderedList.size());

        Assertions.assertEquals(soldListingOld.getSaleDate(), orderedList.get(0).getInitialDate());
        Assertions.assertEquals(LocalDate.of(1997, 5, 31), orderedList.get(0).getFinalDate());

        Assertions.assertEquals(LocalDate.of(2021, 7, 1), orderedList.get(expectedTotalRows-2).getInitialDate());
        Assertions.assertEquals(LocalDate.of(2021, 7, 31), orderedList.get(expectedTotalRows-2).getFinalDate());

        Assertions.assertEquals(LocalDate.of(2021, 8, 1), orderedList.get(expectedTotalRows-1).getInitialDate());
        Assertions.assertEquals(endDate, orderedList.get(expectedTotalRows-1).getFinalDate());
    }

    /**
     * Checks that the returnPaginatedOrderedList method returns a correctly ordered array of sales report rows
     * for year granularity
     */
    @Test
    void testOrderingForYear() {
        List<Sale> salesList = new ArrayList<>();
        salesList.add(soldListingNewOne);
        salesList.add(soldListingNewTwo);
        salesList.add(soldListingOld);

        LocalDate startDate = soldListingOld.getSaleDate();
        LocalDate endDate = soldListingNewTwo.getSaleDate().plusMonths(3);

        Map<LocalDate, SaleReportResponse> map = saleService.granulateSalesHistory(salesList, "YEAR",
                startDate, endDate);

        // Add one because there is an extra year not included by this calculation
        int expectedTotalRows = (int) ChronoUnit.YEARS.between(startDate, endDate) + 1;
        List<SaleReportResponse> orderedList = saleService.returnOrderedList(map, endDate);

        Assertions.assertEquals(expectedTotalRows, orderedList.size());

        Assertions.assertEquals(soldListingOld.getSaleDate(), orderedList.get(0).getInitialDate());
        Assertions.assertEquals(LocalDate.of(1997, 12, 31), orderedList.get(0).getFinalDate());

        Assertions.assertEquals(LocalDate.of(2020, 1, 1), orderedList.get(expectedTotalRows-2).getInitialDate());
        Assertions.assertEquals(LocalDate.of(2020, 12, 31), orderedList.get(expectedTotalRows-2).getFinalDate());

        Assertions.assertEquals(soldListingNewTwo.getSaleDate(), orderedList.get(expectedTotalRows-1).getInitialDate());
        Assertions.assertEquals(endDate, orderedList.get(expectedTotalRows-1).getFinalDate());
    }

    /**
     * Checks the first page produced by returnPaginatedList has the correct number of elements
     */
    @Test
    void testPaginationYearPageZero() {
        List<Sale> salesList = new ArrayList<>();
        salesList.add(soldListingNewOne);
        salesList.add(soldListingNewTwo);
        salesList.add(soldListingOld);

        LocalDate startDate = soldListingOld.getSaleDate();
        LocalDate endDate = soldListingNewTwo.getSaleDate().plusMonths(3);

        Map<LocalDate, SaleReportResponse> map = saleService.granulateSalesHistory(salesList, "YEAR",
                startDate, endDate);

        List<SaleReportResponse> orderedList = saleService.returnOrderedList(map, endDate);

        int pageNumber = 0;
        int pageSize = 10;
        List<SaleReportResponse> paginatedList = saleService.returnPaginatedList(orderedList, pageNumber, pageSize);

        Assertions.assertEquals(pageSize, paginatedList.size());
    }

    /**
     * Checks the second page produced by returnPaginatedList has the correct number of elements
     */
    @Test
    void testPaginationYearPageOne() {
        List<Sale> salesList = new ArrayList<>();
        salesList.add(soldListingNewOne);
        salesList.add(soldListingNewTwo);
        salesList.add(soldListingOld);

        LocalDate startDate = soldListingOld.getSaleDate();
        LocalDate endDate = soldListingNewTwo.getSaleDate().plusMonths(3);

        Map<LocalDate, SaleReportResponse> map = saleService.granulateSalesHistory(salesList, "YEAR",
                startDate, endDate);

        List<SaleReportResponse> orderedList = saleService.returnOrderedList(map, endDate);

        int pageNumber = 1;
        int pageSize = 10;
        List<SaleReportResponse> paginatedList = saleService.returnPaginatedList(orderedList, pageNumber, pageSize);

        Assertions.assertEquals(pageSize, paginatedList.size());
    }

    /**
     * Checks the last page produced by returnPaginatedList has the correct number of elements
     */
    @Test
    void testPaginationYearPageTwo() {
        List<Sale> salesList = new ArrayList<>();
        salesList.add(soldListingNewOne);
        salesList.add(soldListingNewTwo);
        salesList.add(soldListingOld);

        LocalDate startDate = soldListingOld.getSaleDate();
        LocalDate endDate = soldListingNewTwo.getSaleDate().plusMonths(3);

        Map<LocalDate, SaleReportResponse> map = saleService.granulateSalesHistory(salesList, "YEAR",
                startDate, endDate);

        List<SaleReportResponse> orderedList = saleService.returnOrderedList(map, endDate);

        int pageNumber = 2;
        int pageSize = 10;
        List<SaleReportResponse> paginatedList = saleService.returnPaginatedList(orderedList, pageNumber, pageSize);

        Assertions.assertEquals(map.size() - pageNumber*pageSize, paginatedList.size());
    }

    /**
     * Checks the first page for a page size of 100 returned by returnPaginatedList has the correct number of elements
     */
    @Test
    void testPaginationYearPageLargePageSize() {
        List<Sale> salesList = new ArrayList<>();
        salesList.add(soldListingOld);
        salesList.add(soldListingNewOne);
        salesList.add(soldListingNewThree);

        LocalDate startDate = soldListingOld.getSaleDate();
        LocalDate endDate = soldListingNewThree.getSaleDate().plusMonths(3);

        Map<LocalDate, SaleReportResponse> map = saleService.granulateSalesHistory(salesList, "DAY",
                startDate, endDate);

        List<SaleReportResponse> orderedList = saleService.returnOrderedList(map, endDate);
        List<SaleReportResponse> paginatedList = saleService.returnPaginatedList(orderedList, 0, 100);

        Assertions.assertEquals(100, paginatedList.size());
    }

    /**
     * Checks the last page produced by returnPaginatedList has the correct number of elements
     */
    @Test
    void testPaginationYearPageNoResults() {
        List<Sale> salesList = new ArrayList<>();

        LocalDate startDate = soldListingNewThree.getSaleDate().plusMonths(3);
        LocalDate endDate = soldListingNewThree.getSaleDate().plusMonths(3);

        Map<LocalDate, SaleReportResponse> map = saleService.granulateSalesHistory(salesList, "YEAR",
                startDate, endDate);

        // Add one because there is an extra year not included by this calculation
        List<SaleReportResponse> orderedList = saleService.returnOrderedList(map, endDate);
        List<SaleReportResponse> paginatedList = saleService.returnPaginatedList(orderedList, 0, 100);

        Assertions.assertEquals(1, paginatedList.size());
    }

    /**
     * Checks that a page that should have no results is empty
     */
    @Test
    void testPaginationYearPageWrongPage() {
        List<Sale> salesList = new ArrayList<>();
        salesList.add(soldListingDifferentBusiness);
        salesList.add(soldListingDifferentBusiness);
        salesList.add(soldListingDifferentBusiness);

        LocalDate startDate = soldListingDifferentBusiness.getSaleDate();
        LocalDate endDate = soldListingDifferentBusiness.getSaleDate().plusMonths(3);

        Map<LocalDate, SaleReportResponse> map = saleService.granulateSalesHistory(salesList,
                "YEAR", startDate, endDate);

        // Add one because there is an extra year not included by this calculation
        List<SaleReportResponse> orderedList = saleService.returnOrderedList(map, endDate);
        List<SaleReportResponse> paginatedList = saleService.returnPaginatedList(orderedList, 10, 100);

        Assertions.assertEquals(0, paginatedList.size());
    }

    /**
     * Checks that one error is returned when the business is not related to the user
     */
    @Test
    @Transactional
    void testCheckSaleReportParametersInvalidBusinessAndUser() {
        Assertions.assertThrows(ResponseStatusException.class, ()-> saleService.checkSaleReportAuthAndDates(soldListingNewOne.getBusinessId(),
                soldListingOld.getSaleDate(),
                soldListingOld.getSaleDate(), userTwo), "User is not authorised to access this business's sales report!\n");
    }

    /**
     * Checks that no error is returned when the business is related to the user
     */
    @Test
    @Transactional
    void testCheckSaleReportParametersValidBusinessAndUser() {
        Assertions.assertDoesNotThrow(()-> saleService.checkSaleReportAuthAndDates(soldListingDifferentBusinessAndUser.getBusinessId(),
                soldListingOld.getSaleDate(),
                soldListingOld.getSaleDate(),
                userTwo)
        );
    }

    /**
     * Checks that one error is returned when the dates are contradictory
     */
    @Test
    void testCheckSaleReportParametersInvalidDates() {
        Assertions.assertThrows(ResponseStatusException.class, ()-> saleService.checkSaleReportAuthAndDates(soldListingOld.getBusinessId(),
                soldListingNewThree.getSaleDate(),
                soldListingOld.getSaleDate(), userOne),"The start date cannot be after the end date!\n"
                );
    }

    /**
     * Checks that no error is returned when the dates are acceptable
     */
    @Test
    void testCheckSaleReportParametersValidDates() {
        Assertions.assertDoesNotThrow(()-> saleService.checkSaleReportAuthAndDates(soldListingOld.getBusinessId(),
                soldListingOld.getSaleDate(),
                soldListingNewThree.getSaleDate(), userOne)
        );
    }

    /**
     * Checks if sales are returned by getSaleHistoryFromBusinessId.
     */
    @Test
    @Transactional
    void testGettingSalesHistory() {
        Sale sale = saleService.createSaleFromListing(testListing.getId());
        sale = saleRepository.save(sale);

        PaginationInfo<SaleResponse> page = saleService.getSaleHistoryFromBusinessId(sale.getBusinessId(), 1, 100);
        boolean found = false;
        for (SaleResponse saleResponse : page.getPaginationElements()) {
            if (Objects.equals(saleResponse.getId(), sale.getSaleId())) {
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found);
    }

    /**
     * Test for getting SaleGraphResponse from exiting sales within a given start and end dates
     */
    @Test
    void testGettingSaleGraphResponse() {
        LocalDate startDate = soldListingNewThree.getSaleDate();
        LocalDate endDate = soldListingNewThree.getSaleDate();
        SaleGraphResponse response = saleService.getSaleGraphData(testBusinessOne.getId(),startDate, endDate, "YEAR");

        Assertions.assertEquals(testSaleGraphResponse.getLabels(), response.getLabels());
        Assertions.assertEquals(testSaleGraphResponse.getDatasetSale().getLabel(), response.getDatasetSale().getLabel());
        Assertions.assertEquals(testSaleGraphResponse.getDatasetSale().getBackgroundColor(), response.getDatasetSale().getBackgroundColor());
        Assertions.assertEquals(testSaleGraphResponse.getDatasetSale().getData(), response.getDatasetSale().getData());
        Assertions.assertEquals(testSaleGraphResponse.getDatasetRevenue().getLabel(), response.getDatasetRevenue().getLabel());
        Assertions.assertEquals(testSaleGraphResponse.getDatasetRevenue().getBackgroundColor(), response.getDatasetRevenue().getBackgroundColor());
        Assertions.assertEquals(testSaleGraphResponse.getDatasetRevenue().getData(), response.getDatasetRevenue().getData());
    }

}
