package org.seng302.main.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.seng302.main.models.*;
import org.seng302.main.repository.*;
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
class ListingControllerTests {

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
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
    private JSONObject listingJson;

    // Listings Search
    String searchQuery = "?search=moreInfo:'Lots of information'";

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

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
        listingJson = new JSONObject();
        listingJson.put("inventoryItemId", invItem.getId());
        listingJson.put("quantity", listing.getQuantity());
        listingJson.put("price", listing.getPrice());
        listingJson.put("moreInfo", listing.getMoreInfo());
        listingJson.put("closes", listing.getCloses());

    }

    /**
     * Test a valid listing can be added into the database if logged in and business admin.
     */
    @Test
    @WithMockUser
    void testCreateValidListing() throws Exception {

        // Posts the valid business object to the database
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses/" + testBusinessOne.getId() + "/listings")
                        .cookie(businessAdminCookie)
                        .content(objectMapper.writeValueAsString(listingJson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
    }

    /**
     * Test a listing cannot be created if the user is not logged in.
     */
    @Test
    void testCreateListingWhenLoggedOut() throws Exception {

        // Posts the valid business object to the database but expect unauthorized as they are not logged in
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses/" + testBusinessOne.getId() + "/listings")
                        .content(objectMapper.writeValueAsString(listingJson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

    }

    /**
     * Test a listing cannot be created if the business does not exist
     */
    @Test
    @WithMockUser
    void testCreateInvalidBusinessId() throws Exception {

        // Posts the valid business object to the database but expect unauthorized as they are not logged in
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses/" + "50000000" + "/listings")
                        .content(objectMapper.writeValueAsString(listingJson))
                        .cookie(businessAdminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    /**
     * Test a listing cannot be created if user is not business admin
     */
    @Test
    @WithMockUser
    void testCreateListingInvalidBusinessAdmin() throws Exception {

        // Posts the valid business object to the database but expect unauthorized as they are not logged in
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses/" + testBusinessOne.getId() + "/listings")
                        .content(objectMapper.writeValueAsString(listingJson))
                        .cookie(nonBusinessAdminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test a listing cannot be created if invalid inventory item id
     */
    @Test
    @WithMockUser
    void testCreateListingInvalidInventoryItemId() throws Exception {
        listingJson.put("inventoryItemId", 50000);

        // Posts the valid business object to the database but expect unauthorized as they are not logged in
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses/" + testBusinessOne.getId() + "/listings")
                        .content(objectMapper.writeValueAsString(listingJson))
                        .cookie(businessAdminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    /**
     * Test a listing cannot be created if mandatory attributes have not been provided
     */
    @Test
    @WithMockUser
    void testCreateListingMissingAttributes() throws Exception {
        listingJson.put("inventoryItemId", null);
        // Posts the valid business object to the database but expect unauthorized as they are not logged in
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses/" + testBusinessOne.getId() + "/listings")
                        .content(objectMapper.writeValueAsString(listingJson))
                        .cookie(businessAdminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        listingJson.put("inventoryItemId", invItem.getId());
        listingJson.put("quantity", null);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses/" + testBusinessOne.getId() + "/listings")
                        .content(objectMapper.writeValueAsString(listingJson))
                        .cookie(businessAdminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        listingJson.put("quantity", listing.getQuantity());
        listingJson.put("price", null);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses/" + testBusinessOne.getId() + "/listings")
                        .content(objectMapper.writeValueAsString(listingJson))
                        .cookie(businessAdminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    /**
     * {
     * "totalPages": 0,
     * "totalElements": 0
     * "paginationElements": [cards data according to section]
     * }
     * Check if Json return in that format
     */
    @Test
    @WithMockUser
    void testGetPaginationInformation() throws Exception {

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/businesses/" + testBusinessOne.getId() + "/listings")
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
     * Testing searching for listings with user not logged in
     */
    @Test
    @WithMockUser
    public void testListingsSearchTypeNotLoggedIn() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/listings" + searchQuery)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    /**
     * Testing searching for listings with valid data
     */
    @Test
    @WithMockUser
    public void testListingsSearchTypeQuery() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/listings" + searchQuery)
                .cookie(nonBusinessAdminCookie)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Test getting one listing (valid)
     */
    @Test
    @WithMockUser
    public void testGetOneListing() throws Exception {
        listing = listingRepository.save(listing);

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/listings/" + listing.getId())
                        .cookie(nonBusinessAdminCookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject jsonResult = (JSONObject) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(result.getResponse().getContentAsString());
        Assertions.assertEquals(jsonResult.get("id"), listing.getId());
    }

    /**
     * Test getting one listing returned 404 when listing is invalid
     */
    @Test
    @WithMockUser
    public void testGetOneListingInvalid() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/listings/" + 10000000)
                .cookie(nonBusinessAdminCookie)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotAcceptable());
    }

    /**
     * Test that buying an existing listing with an existing, logged in, user returns 200
     * Endpoint being tested: PUT /listings/:id
     */
    @Test
    @WithMockUser
    public void testBuyListingValid() throws Exception {
        listing = listingRepository.save(listing);
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/listings/" + listing.getId())
                        .cookie(nonBusinessAdminCookie))
                .andExpect(status().isOk());
    }

    /**
     * Test that buying a non-existing listing with an existing, logged in, user returns 406 (not acceptable)
     * Endpoint being tested: PUT /listings/:id
     */
    @Test
    @WithMockUser
    public void testBuyListingInValidNoListingExists() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/listings/" + 10000000)
                        .cookie(nonBusinessAdminCookie))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Test that buying an existing listing with a non-existing, logged in, user returns 400 (bad request)
     * Endpoint being tested: PUT /listings/:id
     */
    @Test
    @WithMockUser
    public void testBuyListingInValidNoUserExistsWithSessionId() throws Exception {
        listing = listingRepository.save(listing);
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/listings/" + 1)
                        .cookie(invalidCookie))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test that buying an existing listing with a logged out user returns 401 (not authorised)
     * Endpoint being tested: PUT /listings/:id
     */
    @Test
    @WithMockUser
    public void testBuyListingInValidNotLoggedIn() throws Exception {
        listing = listingRepository.save(listing);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/listings/" + 1))
                .andExpect(status().isUnauthorized());
    }

}
