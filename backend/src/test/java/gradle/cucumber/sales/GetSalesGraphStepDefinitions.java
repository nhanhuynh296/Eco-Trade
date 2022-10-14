package gradle.cucumber.sales;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.seng302.main.dto.response.SaleGraphResponse;
import org.seng302.main.dto.response.SaleGraphRevenueDatasetResponse;
import org.seng302.main.dto.response.SaleGraphSaleDatasetResponse;
import org.seng302.main.models.*;
import org.seng302.main.repository.*;
import org.seng302.main.services.BusinessTypeService;
import org.seng302.main.services.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

/**
 * The step definitions for the U42 Sales graph
 */
public class GetSalesGraphStepDefinitions {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    SaleRepository saleRepository;
    @Autowired
    SaleService saleService;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    Business testBusiness;
    User testUser;
    Address testAddressOne;
    Address testAddressTwo;
    Product testProduct;
    Sale testSale;

    SaleGraphResponse testSaleGraphResponse;
    SaleGraphResponse actualSaleGraphResponse = new SaleGraphResponse();

    /**
     * Set up the testing database entities
     */
    public void createSale() {
        testAddressTwo = new Address("3/24", "Ilam Road", "Christchurch",
                "Canterbury", "New Zealand", "90210");

        testBusiness = new Business(testUser.getId(), "Some Business", "Description", testAddressTwo, BusinessTypeService.ACCOMMODATION.toString(), LocalDate.now());
        testBusiness = businessRepository.save(testBusiness);

        testProduct = new Product(testBusiness.getId(), testBusiness, "product", "description", "manufacturer", 0.1, LocalDate.now());
        testProduct = productRepository.save(testProduct);

        testSale = new Sale(testBusiness.getId(), testProduct, LocalDate.of(2021, 5, 10), LocalDate.of(2021, 5, 10), 1, 1L, 1.0);
        testSale = saleRepository.save(testSale);

        testSaleGraphResponse = new SaleGraphResponse();
        testSaleGraphResponse.setLabels(List.of("2021"));

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

    @Given("I am logged in as a business admin with one sale")
    public void iAmLoggedInAsABusinessAdminWithTwoSales() {
        testAddressOne = new Address("3/25", "Ilam Road", "Christchurch",
                "Canterbury", "New Zealand", "90210");
        testUser = new User("John", "", "Johnson", "Johnny", "empty Bio", "email@here.com.co",
                LocalDate.of(1970, 1, 1), "0200 9020", testAddressOne, LocalDate.now(), "ROLE_USER",
                passwordEncoder.encode("pass"), null, null);
        testUser = userRepository.save(testUser);
        createSale();
    }

    @When("I retrieve my business' sales report graph")
    public void iRetrieveSaleReportData() {
        LocalDate startDate = LocalDate.of(2021, 1, 1);
        LocalDate endDate = LocalDate.of(2021, 12, 31);
        actualSaleGraphResponse = saleService.getSaleGraphData(testSale.getBusinessId(), startDate, endDate, "YEAR");
    }

    @Then("listings that have been bought from my business are returned in a graph format")
    public void iCanViewSaleReportGraphData() {
        Assertions.assertEquals(testSaleGraphResponse.getLabels(), actualSaleGraphResponse.getLabels());
        Assertions.assertEquals(testSaleGraphResponse.getDatasetSale().getLabel(), actualSaleGraphResponse.getDatasetSale().getLabel());
        Assertions.assertEquals(testSaleGraphResponse.getDatasetSale().getBackgroundColor(), actualSaleGraphResponse.getDatasetSale().getBackgroundColor());
        Assertions.assertEquals(testSaleGraphResponse.getDatasetSale().getData(), actualSaleGraphResponse.getDatasetSale().getData());
        Assertions.assertEquals(testSaleGraphResponse.getDatasetRevenue().getLabel(), actualSaleGraphResponse.getDatasetRevenue().getLabel());
        Assertions.assertEquals(testSaleGraphResponse.getDatasetRevenue().getBackgroundColor(), actualSaleGraphResponse.getDatasetRevenue().getBackgroundColor());
        Assertions.assertEquals(testSaleGraphResponse.getDatasetRevenue().getData(), actualSaleGraphResponse.getDatasetRevenue().getData());
    }

}
