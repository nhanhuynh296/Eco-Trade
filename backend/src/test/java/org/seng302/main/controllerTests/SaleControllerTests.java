package org.seng302.main.controllerTests;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.seng302.main.models.*;
import org.seng302.main.repository.*;
import org.seng302.main.services.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
public class SaleControllerTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private SaleService saleService;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    private final LocalDate localDate = LocalDate.now();

    private Cookie businessAdminCookie; //Logged in cookie
    private Cookie nonBusinessAdminCookie;
    private Cookie invalidCookie = new Cookie("JSESSIONID", "INVALID");

    // Address for business
    private final Address validAddress = new Address("3/24", "Ilam Road", "Christchurch",
            "Canterbury", "New Zealand", "90210");

    // Valid business entity for testing product catalogue
    private Business testBusinessOne;

    private InventoryItem invItem;

    private Listing listing;

    private Sale sale;

    /**
     * Enter initial objects into database
     */
    @BeforeEach
    public void initialiseDatabases() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        userRepository.deleteAll();

        User user = new User("Totally", "Real", "Name", "TRN", "Hello There", "cja128@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "", "", "", ""), LocalDate.now(), "ROLE_USER", "password123", "456", null);
        User nonBusinessAdminUser = new User("Non", "Business", "Admin", "Hi", "Welcome", "abc@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "", "", "AAA", ""), LocalDate.now(), "ROLE_USER", "bbb", "111", null);
        User globalAdminUser = new User("Admin", "", "Admin", "A", "A", "admin@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "", "", "", ""), LocalDate.now(), "ROLE_DEFAULT_ADMIN", "password123", "789", null);

        user = userRepository.save(user);
        userRepository.save(nonBusinessAdminUser);
        userRepository.save(globalAdminUser);

        businessAdminCookie = new Cookie("JSESSIONID", "456");
        nonBusinessAdminCookie = new Cookie("JSESSIONID", "111");

        testBusinessOne = new Business(user.getId(), "Varrock Grand Exchange", "Description1", validAddress, "Accomodation", localDate);
        testBusinessOne = businessRepository.save(testBusinessOne);

        Product product = new Product(testBusinessOne.getId(), testBusinessOne, "Some Product", "Description",
                "TestCo.", 12d, LocalDate.now());
        product = productRepository.save(product);

        invItem = new InventoryItem(product, 10, 1d, 10d, null, null, null, LocalDate.now().plusDays(2));
        invItem = inventoryRepository.save(invItem);

        listing = new Listing(invItem, 5, 5.5, "Lots of information", localDate, LocalDate.now().plusDays(1));
        listing = listingRepository.save(listing);

        sale = saleService.createSaleFromListing(listing.getId());
        sale = saleRepository.save(sale);
    }

    /**
     * {
     * "totalPages": 0,
     * "totalElements": 0
     * "paginationElements": [sales]
     * }
     * Check if a Json is returned in that format
     */
    @Test
    @WithMockUser
    void testGetPaginationInformation() throws Exception {

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/businesses/" + testBusinessOne.getId() + "/saleshistory")
                        .cookie(businessAdminCookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JSONObject jsonResult = (JSONObject) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(result.getResponse().getContentAsString());
        Assertions.assertTrue(jsonResult.containsKey("paginationElements"));
        Assertions.assertTrue(jsonResult.containsKey("totalPages"));
        Assertions.assertTrue(jsonResult.containsKey("totalElements"));
    }

    /**
     * Check if the correct sale is returned in the paginationElements list
     */
    @Test
    @WithMockUser
    void testGetSales() throws Exception {

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/businesses/" + testBusinessOne.getId() + "/saleshistory")
                        .cookie(businessAdminCookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JSONObject jsonResult = (JSONObject) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(result.getResponse().getContentAsString());
        Assertions.assertTrue(jsonResult.containsKey("paginationElements"));
        Assertions.assertTrue(jsonResult.containsKey("totalPages"));
        Assertions.assertTrue(jsonResult.containsKey("totalElements"));
        Long responseId = (Long) ((JSONObject) (((JSONArray) jsonResult.get("paginationElements")).get(0))).get("id");
        Assertions.assertEquals(sale.getSaleId(), responseId);
    }

    /**
     * Testing getting sale history when not logged in returns 403
     */
    @Test
    @WithMockUser
    void testSalesNotLoggedIn() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/businesses/" + testBusinessOne.getId() + "/saleshistory")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test that getting sales history from a non existing business
     * with an existing, logged in, user returns 406 (not acceptable)
     */
    @Test
    @WithMockUser
    void testGetSalesHistoryInValidNoBusinessExists() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/businesses/" + 100000 + "/saleshistory")
                        .cookie(nonBusinessAdminCookie))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Test that getting a sales history with a non-existing, logged in, user returns 400 (bad request)
     * Endpoint being tested: PUT /listings/:id
     */
    @Test
    @WithMockUser
    void testBuyListingInValidNoUserExistsWithSessionId() throws Exception {
        listing = listingRepository.save(listing);
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/businesses/" + testBusinessOne.getId() + "/saleshistory")
                        .cookie(invalidCookie))
                .andExpect(status().isBadRequest());
    }

    /**
     * Testing getting sale history graph when not logged in returns 400
     */
    @Test
    @WithMockUser
    void testSalesReportGraphNotLoggedIn() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/businesses/" + testBusinessOne.getId() + "/salesgraph")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test that getting sales history graph from a non existing business
     * with an existing, logged in, user returns 400 bad request
     */
    @Test
    @WithMockUser
    void testGetSalesHistoryGraphInValidNoBusinessExists() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/businesses/" + 0 + "/salesgraph")
                .cookie(nonBusinessAdminCookie))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test that getting a sales history graph with a non-existing, logged in, user returns 400 (bad request)
     */
    @Test
    @WithMockUser
    void testGetSaleReportGraphInValidNoUserExistsWithSessionId() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/businesses/" + testBusinessOne.getId() + "/salesgraph")
                .cookie(invalidCookie))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test that getting sales history graph from a non existing business
     * with an existing, logged in, user returns 400 (bad request)
     */
    @Test
    @WithMockUser
    void testGetSalesGraphInValidNoBusinessExists() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/businesses/" + 100000 + "/salesgraph")
                .cookie(nonBusinessAdminCookie))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests that the granularity is not required in the params
     */
    @Test
    @WithMockUser
    void testGranularityDefaultsToYear() throws Exception {
        listing = listingRepository.save(listing);
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/businesses/" + testBusinessOne.getId() + "/salesgraph")
                .param("startDateString", "2020-09-22")
                .param("endDateString", "2020-09-22")
                .cookie(businessAdminCookie))
                .andExpect(status().isOk());
    }

    /**
     * Tests that the granularity is not required in the params
     */
    @Test
    @WithMockUser
    void testSalesReportReturnsOk() throws Exception {
        listing = listingRepository.save(listing);
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/businesses/" + testBusinessOne.getId() + "/salesreport")
                .param("startDateString", "2020-09-22")
                .param("endDateString", "2020-09-22")
                .cookie(businessAdminCookie))
                .andExpect(status().isOk());
    }
}
