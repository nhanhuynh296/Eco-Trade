package org.seng302.main.controllerTests;


import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.models.*;
import org.seng302.main.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
class InventoryControllerTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    private final LocalDate localDate = LocalDate.now();

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Cookie businessAdminCookie; //Logged in cookie
    private Cookie nonBusinessAdminCookie;
    private Cookie adminCookie; //Admin cookie

    // Address for business
    private final Address validAddress = new Address("3/24", "Ilam Road", "Christchurch",
            "Canterbury", "New Zealand", "90210");

    // Valid business entity for testing product catalogue
    private Business testBusinessOne;

    private Product product;
    private InventoryItem invItem;

    private JSONObject itemJson;


    /**
     * Enter initial objects into database
     */
    @BeforeEach
    public void populateInventoryItems() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        //Remove existing
        userRepository.deleteAll();

        User user = new User("Totally", "Real", "Name", "TRN", "Hello There", "cja128@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "", "", "", ""), LocalDate.now(), "ROLE_USER", "password123", "456", null);
        User nonBusinessAdminUser = new User("Non", "Business", "Admin", "Hi", "Welcome", "abc@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "", "", "AAA", ""), LocalDate.now(), "ROLE_USER", "bbb", "111", null);
        User globalAdminUser = new User("Admin", "", "Admin", "A", "A", "admin@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "", "", "", ""), LocalDate.now(), "ROLE_DEFAULT_ADMIN", "password123", "789", null);

        user = userRepository.save(user);
        userRepository.save(nonBusinessAdminUser);
        userRepository.save(globalAdminUser);

        businessAdminCookie = new Cookie("JSESSIONID", "456");
        nonBusinessAdminCookie = new Cookie("JSESSIONID", "111");
        adminCookie = new Cookie("JSESSIONID", "789");

        testBusinessOne = new Business(user.getId(), "Varrock Grand Exchange", "Description1", validAddress, "Accomodation", localDate);
        testBusinessOne = businessRepository.save(testBusinessOne);

        product = new Product(testBusinessOne.getId(), testBusinessOne, "Some Product", "Description",
                "TestCo.", 12d, LocalDate.now());

        product = productRepository.save(product);

        ProductImage imageOne = new ProductImage("~/fileName.test", "~/thumbnailFile.test");
        ProductImage imageTwo = new ProductImage("~/otherFile.should.be.here", "~/otherThumb.also.here");

        imageOne.setProduct(product);
        imageTwo.setProduct(product);
        List<ProductImage> imageList = List.of(imageOne, imageTwo);
        product.setImages(imageList);

        productImageRepository.save(imageOne);
        productImageRepository.save(imageTwo);

        invItem = new InventoryItem(product, 10, 1d, 10d, null, null, null, LocalDate.now());
        invItem = inventoryRepository.save(invItem);

        itemJson = new JSONObject();
        itemJson.put("productId", invItem.getProduct().getId().intValue());
        itemJson.put("quantity", invItem.getQuantity());
        itemJson.put("pricePerItem", invItem.getPricePerItem());
        itemJson.put("totalPrice", invItem.getTotalPrice());
        itemJson.put("manufactured", invItem.getManufactured());
        itemJson.put("sellBy", invItem.getSellBy());
        itemJson.put("bestBefore", invItem.getBestBefore());
        itemJson.put("expires", invItem.getExpires().toString());
    }

    /**
     * Tests getting inventory as an unauthorized user
     */
    @Test
    @WithMockUser
    void testNotLoggedInGetInventory() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/businesses/" + testBusinessOne.getId() + "/inventory")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests getting inventory as an authorized user but not business admin
     */
    @Test
    @WithMockUser
    void testLoggedInWrongUser() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/businesses/" + testBusinessOne.getId() + "/inventory")
                        .cookie(nonBusinessAdminCookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests getting inventory as an authorized user who is the business admin
     */
    @Test
    @WithMockUser
    void testLoggedInBusinessAdmin() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/businesses/" + testBusinessOne.getId() + "/inventory")
                        .cookie(businessAdminCookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Tests getting inventory and returned JSON data as business admin
     */
    @Test
    @WithMockUser
    void testLoggedInBusinessAdminContentsCorrectTest() throws Exception {
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/businesses/" + testBusinessOne.getId() + "/inventory")
                        .cookie(businessAdminCookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Because of the order that the JSON could be returned in + using dates, we can't directly compare the result
        String shouldContainOne = "\"filename\":\"~/otherFile.should.be.here\"";
        String shouldContainTwo = "\"thumbnailFilename\":\"~/thumbnailFile.test\"";
        String shouldContainThree = "\"sellBy\":null";
        String jsonResult = result.getResponse().getContentAsString();

        Assertions.assertNotEquals("", jsonResult);
        Assertions.assertTrue(jsonResult.contains(shouldContainOne));
        Assertions.assertTrue(jsonResult.contains(shouldContainTwo));
        Assertions.assertTrue(jsonResult.contains(shouldContainThree));
    }

    /**
     * Tests getting inventory and returned JSON data as global admin
     */
    @Test
    @WithMockUser
    void testLoggedInGAAContentsCorrectTest() throws Exception {
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/businesses/" + testBusinessOne.getId() + "/inventory")
                        .cookie(adminCookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Because of the order that the JSON could be returned in + using dates, we can't directly compare the result
        String shouldContainOne = "\"filename\":\"~/otherFile.should.be.here\"";
        String shouldContainTwo = "\"thumbnailFilename\":\"~/thumbnailFile.test\"";
        String shouldContainThree = "\"sellBy\":null";
        String jsonResult = result.getResponse().getContentAsString();

        Assertions.assertNotEquals("", jsonResult);
        Assertions.assertTrue(jsonResult.contains(shouldContainOne));
        Assertions.assertTrue(jsonResult.contains(shouldContainTwo));
        Assertions.assertTrue(jsonResult.contains(shouldContainThree));
    }

    /**
     * Tests getting inventory from invalid business ID as a global admin
     */
    @Test
    @WithMockUser
    void testLoggedInGAANotAcceptableBusiness() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/businesses/-1/inventory")
                        .cookie(adminCookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Tests updating inventory item with not authorized user
     */
    @Test
    @WithMockUser
    @Transactional
    void testNotAuthorizedUserUpdateInventoryItem() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/businesses/" + testBusinessOne.getId() + "/inventory/" + invItem.getId())
                        .content(objectMapper.writeValueAsString(invItem))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests updating inventory item with invalid item details
     */
    @Test
    @WithMockUser
    void testInvalidInventoryItemUpdateInventoryItem() throws Exception {
        itemJson.put("quantity", -1);
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/businesses/" + testBusinessOne.getId() + "/inventory/" + invItem.getId())
                        .cookie(adminCookie)
                        .content(String.valueOf(itemJson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests updating inventory item with not an admin of the business and not a DGAA
     */
    @Test
    @WithMockUser
    void testNotBusinessAdminNotDGAAUpdateInventoryItem() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/businesses/" + testBusinessOne.getId() + "/inventory/" + invItem.getId())
                        .cookie(nonBusinessAdminCookie)
                        .content(objectMapper.writeValueAsString(itemJson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests updating inventory item with valid requirements
     */
    @Test
    @WithMockUser
    void testValidRequestUpdateInventoryItem() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/businesses/" + testBusinessOne.getId() + "/inventory/" + invItem.getId())
                        .cookie(adminCookie)
                        .content(objectMapper.writeValueAsString(itemJson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/businesses/" + testBusinessOne.getId() + "/inventory/" + invItem.getId())
                        .cookie(businessAdminCookie)
                        .content(objectMapper.writeValueAsString(itemJson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Tests posting a valid inventory item as a unauthroized user
     */
    @Test
    @WithAnonymousUser
    void notLoggedInAddInventoryTest() throws Exception {

        InventoryItem testItem = new InventoryItem(product, 10, 1d, 10d, null, null, null, LocalDate.now());

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses/" + testBusinessOne.getId() + "/inventory")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests an invalid product cannot be added into a business catalogue
     */
    @Test
    void testInvalidProductIntoBusiness() throws Exception {


        InventoryItem testItem = new InventoryItem(product, 10, 1d, 10d, null, null, null, null);

        //Posts the valid product to the business catalogue
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses/" + testBusinessOne.getId() + "/inventory")
                        .cookie(businessAdminCookie)
                        .content(objectMapper.writeValueAsString(testItem))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Assertions.assertFalse(inventoryRepository.findAll().contains(testItem));
    }

    /**
     * {
     * "totalPages": 0,
     * "totalElements": 0
     * "content": [business inventory data according to section]
     * }
     * Check if Json return in that format
     */
    @Test
    @WithMockUser
    void testGetPaginationInformation() throws Exception {

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/businesses/" + testBusinessOne.getId() + "/inventory")
                        .cookie(businessAdminCookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JSONObject jsonResult = (JSONObject) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(result.getResponse().getContentAsString());
        Assertions.assertTrue(jsonResult.containsKey("paginationElements"));
        Assertions.assertTrue(jsonResult.containsKey("totalPages"));
        Assertions.assertTrue(jsonResult.containsKey("totalElements"));

    }

}
