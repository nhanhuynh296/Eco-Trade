package gradle.cucumber.sales;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.dto.response.SaleReportResponse;
import org.seng302.main.models.*;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.ProductRepository;
import org.seng302.main.repository.SaleRepository;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.SaleService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The step definitions for the U41 Sales report
 */
public class GetSalesReportStepDefinitions {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleService saleService;

    private User user;
    private Sale saleOne;
    private Sale saleTwo;
    private LocalDate startDate;
    private LocalDate endDate;
    private PaginationInfo<SaleReportResponse> results;
    List<Sale> saleList;

    void createSale() {
        Address address2 = new Address("5", "Somewhere", "Over", "The", "Rainbow", "800");
        Business testBusiness = new Business(user.getId(), "Lumbridge General Store", "Description1", address2,
                "Accomodation", LocalDate.now());
        testBusiness = businessRepository.save(testBusiness);

        Product testProduct = new Product(testBusiness.getId(), testBusiness, "Watties Baked Beans", "Baked beans in a can",
                "Heinz Wattie's Limited", 2.2, LocalDate.now());
        testProduct = productRepository.save(testProduct);

        saleOne = new Sale(testBusiness.getId(), testProduct,
                LocalDate.of(1997, 5, 10),
                LocalDate.of(1997, 5, 10),
                100, 200L, 20.5);
        saleOne = saleRepository.save(saleOne);

        saleTwo =  new Sale(testBusiness.getId(), testProduct,
                LocalDate.of(2021, 2, 2),
                LocalDate.of(2021, 2, 2),
                951, 49L, 56.0);
        saleTwo = saleRepository.save(saleTwo);
    }

    @Given("I am logged in as a business admin with two sales")
    public void iAmLoggedInAsABusinessAdminWithTwoSales() {
        Address address = new Address("1", "Nowhere", "Varrock", "Tirannwn", "Gilenor", "800");
        user = new User("A", "A", "A", "A", "A", "cja128@uclive.ac.nz",
                LocalDate.now(), "", address, LocalDate.now(), "ROLE_USER", "password123", "55555", null);
        user = userRepository.save(user);
        createSale();
        saleList = new ArrayList<>();
        saleList.add(saleOne);
        saleList.add(saleTwo);
    }

    @When("I retrieve my business's sales report with custom start date {string} and end date {string}")
    @Transactional
    public void iRetrieveMyBusinessSSalesReportWithCustomStartDateAndEndDate(String startDateStr, String endDateStr) {
        startDate = LocalDate.parse(startDateStr);
        endDate = LocalDate.parse(endDateStr);

        Map<LocalDate, SaleReportResponse> unorderedResults = saleService.granulateSalesHistory(saleList, "YEAR", startDate, endDate);

        int expectedTotalResults = 24;
        results = saleService.returnPaginatedOrderedList(unorderedResults, 0, expectedTotalResults, endDate);

        Assertions.assertEquals(expectedTotalResults, results.getPaginationElements().size());
    }

    @Then("the first and last row contain the specified dates")
    public void theFirstAndLastRowContainsTheSpecifiedDates() {
        SaleReportResponse firstRow = results.getPaginationElements().get(1);
        SaleReportResponse lastRow = results.getPaginationElements().get(results.getPaginationElements().size() - 1);

        Assertions.assertEquals(startDate, firstRow.getInitialDate());
        Assertions.assertEquals(endDate, lastRow.getFinalDate());
    }

    @When("I retrieve my business's sales report with granularity {string}")
    public void iRetrieveMyBusinessSalesReportWithGranularity(String granulateBy) {
        results = saleService.getSaleReportData(granulateBy, saleOne.getBusinessId(), 0, 10000,
                LocalDate.of(1997, 5, 10), LocalDate.of(2021, 2, 2));

        Assertions.assertTrue(results.getPaginationElements().size() > 0);
    }

    @Then("the correct number of rows are shown")
    public void theCorrectNumberOfRowsAreShown() {
        List<Integer> expectedTotalResults = new ArrayList<>(4);
        expectedTotalResults.add(25);
        expectedTotalResults.add(286);
        expectedTotalResults.add(1240);
        expectedTotalResults.add(8670);

        Assertions.assertTrue(expectedTotalResults.contains(results.getPaginationElements().size()));
    }

    @When("I request sale report data with a time period from {string} to {string}")
    public void iRequestSaleReportDataWithATimePeriodFromTo(String startDateStr, String endDateStr) {
        startDate = LocalDate.parse(startDateStr);
        endDate = LocalDate.parse(endDateStr);
        results = saleService.getSaleReportData("YEAR", saleOne.getBusinessId(), 0, 100, startDate, endDate);

        Assertions.assertTrue(results.getPaginationElements().size() > 0);
    }

    @Then("the report end date will be changed to the start date plus 100 years")
    public void thenTheReportStartDateWillBeChangedToTheEndDateYear() {
        Assertions.assertEquals(Integer.parseInt(results.getPaginationElements().get(results.getPaginationElements().size() - 1).getPeriod()), endDate.getYear() - 100);
        Assertions.assertEquals(Integer.parseInt(results.getPaginationElements().get(0).getPeriod()), startDate.getYear());
    }
}
