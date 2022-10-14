package gradle.cucumber.sales;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.dto.response.SaleResponse;
import org.seng302.main.models.*;
import org.seng302.main.repository.*;
import org.seng302.main.services.SaleService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.time.LocalDate;

public class GetSalesHistoryStepDefinitions {

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
    private SaleRepository saleRepository;

    @Autowired
    private SaleService saleService;

    private User user;
    private Listing testListing;
    private Sale sale;
    PaginationInfo<SaleResponse> page;

    @Given("I am logged in as a business admin")
    public void i_am_logged_in_as_a_business_admin() {
        Address address = new Address("1", "Nowhere", "Varrock", "Tirannwn", "Gilenor", "800");
        user = new User("A", "A", "A", "A", "A", "cja128@uclive.ac.nz",
                LocalDate.now(), "", address, LocalDate.now(), "ROLE_USER", "password123", "55555", null);
        user = userRepository.save(user);
    }

    @When("I retrieve my business' sales history")
    @Transactional
    public void i_retrieve_my_business_sales_history() {
        createSale();
        page = saleService.getSaleHistoryFromBusinessId(sale.getBusinessId(), 1, 1);
    }

    @Then("listings that have been bought from my business are returned")
    public void listings_that_have_been_bought_from_my_business_are_returned() {
        SaleResponse response = page.getPaginationElements().get(0);
        Assertions.assertEquals(sale.getBusinessId(), response.getBusinessId());
        Assertions.assertEquals(sale.getSaleId(), response.getId());
    }


    void createSale() {
        Address address2 = new Address("5", "Somewhere", "Over", "The", "Rainbow", "800");
        Business testBusiness = new Business(user.getId(), "Lumbridge General Store", "Description1", address2,
                "Accomodation", LocalDate.now());
        testBusiness = businessRepository.save(testBusiness);

        Product testProduct = new Product(testBusiness.getId(), testBusiness, "Watties Baked Beans", "Baked beans in a can",
                "Heinz Wattie's Limited", 2.2, LocalDate.now());
        testProduct = productRepository.save(testProduct);

        InventoryItem testInventory = new InventoryItem(testProduct, 69, 10.0, 100.0,
                LocalDate.now(), null, null, LocalDate.now().plusDays(20));
        testInventory = inventoryRepository.save(testInventory);

        testListing = new Listing(testInventory, 2, 4.0, "Beans",
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(5));
        testListing = listingRepository.save(testListing);

        sale = saleService.createSaleFromListing(testListing.getId());
        sale = saleRepository.save(sale);
    }
}
