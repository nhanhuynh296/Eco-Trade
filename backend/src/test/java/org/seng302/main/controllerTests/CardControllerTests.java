package org.seng302.main.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.messages.internal.com.google.gson.Gson;
import io.cucumber.messages.internal.com.google.gson.JsonArray;
import io.cucumber.messages.internal.com.google.gson.JsonObject;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.seng302.main.dto.request.CardRequest;
import org.seng302.main.models.Address;
import org.seng302.main.models.Card;
import org.seng302.main.models.Keyword;
import org.seng302.main.models.User;
import org.seng302.main.repository.CardRepository;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
class CardControllerTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    // Cookies
    private Cookie cookieInvalid; //Logged in cookie
    private Cookie cookieValid; //Logged in cookie
    private Cookie cookieValid2; //Logged in cookie
    private Cookie adminCookie; //Admin cookie

    // Users
    private User invalidUser;
    private User validUser;
    private User validUser2;
    private User validUser3;
    private User globalAdminUser;

    private Card testCard1;
    private Card testCard2;
    private Card testCard3;

    // Card data
    private final JSONObject cardObject = new JSONObject();
    private final String validTitle = "Food";
    private final String validDescription = "Delicious";
    private final String section = "ForSale";

    //Message object
    private final CardRequest cardRequest = new CardRequest();


    /**
     * Before each test tje card and user repos are cleared, users are saved, cookies are created, and cardObject is created
     */
    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        //Remove existing users
        cardRepository.deleteAll();
        userRepository.deleteAll();
        // Users
        User invalidUser = new User("A", "A", "A", "A", "A", "user1@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "", "", "", ""), LocalDate.now(), "ROLE_USER", "password123", "666", null);
        validUser = new User("A", "A", "A", "A", "A", "cja128@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "TestCity", "TestRegion", "TestCountry", ""), LocalDate.now(), "ROLE_USER", "password123", "123", null);
        User validUser2 = new User("A", "A", "A", "A", "A", "testUser2@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "TestCity2", "TestRegion2", "TestCountry2", ""), LocalDate.now(), "ROLE_USER", "password123", "789", null);
        User validUser3 = new User("A", "A", "A", "A", "A", "testUser3@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "TestCity3", "TestRegion3", "TestCountry3", ""), LocalDate.now(), "ROLE_USER", "password123", "789", null);
        User globalAdminUser = new User("A", "A", "A", "A", "A", "admin@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "", "", "", ""), LocalDate.now(), "ROLE_DEFAULT_ADMIN", "password123", "456", null);

        userRepository.save(invalidUser);
        validUser = userRepository.save(validUser);
        validUser2 = userRepository.save(validUser2);
        validUser3 = userRepository.save(validUser3);
        userRepository.save(globalAdminUser);

        cookieInvalid = new Cookie("JSESSIONID", "666");
        cookieValid = new Cookie("JSESSIONID", "123");
        adminCookie = new Cookie("JSESSIONID", "456");

        JSONArray keywordArray = new JSONArray();
        cardObject.put("creatorId", validUser.getId());
        cardObject.put("keywords", keywordArray);

        List<Keyword> keywordList = new ArrayList<>();
        testCard1 = new Card(validUser, "ForSale", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), "Car", "Subaru", keywordList);
        testCard2 = new Card(validUser2, "ForSale", LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2), "Boat", "Subaru", keywordList);
        testCard3 = new Card(validUser3, "ForSale", LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2), "Train", "Subaru", keywordList);
        testCard1 = cardRepository.save(testCard1);
        testCard2 = cardRepository.save(testCard2);
        testCard3 = cardRepository.save(testCard3);
    }

    /**
     * Tests creating new card with the user not logged in
     */
    @Test
    @WithAnonymousUser
    void testUserIsNotLoggedInCreateCard() throws Exception {
        cardObject.put("section", section);
        cardObject.put("title", validTitle);
        cardObject.put("description", validDescription);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/cards")
                        .content(objectMapper.writeValueAsString(cardObject))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests creating new card with invalid card details
     */
    @Test
    void testInvalidCardDetailsCreateCard() throws Exception {
        cardObject.put("section", null);
        String longTitle = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        cardObject.put("title", longTitle);
        String longDescription = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        cardObject.put("description", longDescription);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/cards")
                        .cookie(adminCookie)
                        .content(objectMapper.writeValueAsString(cardObject))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests creating new card with current user not being GAA and not the card user
     */
    @Test
    void testInvalidCurrentUserCreateCard() throws Exception {
        cardObject.put("section", section);
        cardObject.put("title", validTitle);
        cardObject.put("description", validDescription);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/cards")
                        .cookie(cookieInvalid)
                        .content(objectMapper.writeValueAsString(cardObject))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests creating new card with current user being GAA and not card user
     */
    @Test
    void testGAACurrentUserCreateCard() throws Exception {
        cardObject.put("section", section);
        cardObject.put("title", validTitle);
        cardObject.put("description", validDescription);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/cards")
                        .cookie(adminCookie)
                        .content(objectMapper.writeValueAsString(cardObject))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    /**
     * Tests creating new card with valid user and details
     */
    @Test
    void testValidUserCreateCard() throws Exception {
        cardObject.put("section", section);
        cardObject.put("title", validTitle);
        cardObject.put("description", validDescription);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/cards")
                        .cookie(cookieValid)
                        .content(objectMapper.writeValueAsString(cardObject))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }


    /**
     * Tests getting cards as an unauthorized user, not logged in
     */
    @Test
    @WithMockUser
    void testNotLoggedInGetCards() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/cards?section=ForSale")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    /**
     * Tests getting cards as an authorized user, correct section
     */
    @Test
    @WithMockUser
    void testLoggedInGetCards() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/cards?section=ForSale")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    /**
     * Tests getting cards as global admin
     */
    @Test
    @WithMockUser
    void testGlobalAdminGetCards() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/cards?section=ForSale")
                        .cookie(adminCookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    /**
     * Tests getting inventory as an authorized user, incorrect section
     */
    @Test
    @WithMockUser
    void testLoggedInDontGetCards() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/cards?section=Error")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /**
     * Tests getting inventory as an authorized user, correct card section returned
     */
    @Test
    @WithMockUser
    void testGetCorrectCardSection() throws Exception {

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/cards?section=ForSale")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String shouldContainSection = "\"section\":\"ForSale\"";
        String jsonResult = result.getResponse().getContentAsString();
        Assertions.assertNotEquals("", jsonResult);
        Assertions.assertTrue(jsonResult.contains(shouldContainSection));
    }


    /**
     * Tests getting cards as an authorized user, correct card fully returned
     */
    @Test
    @WithMockUser
    void testGetCorrectCardOverall() throws Exception {

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/cards?section=ForSale")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String shouldNotContainSection = "\"section\":\"Wanted\"";
        String shouldNotContainExchangeSection = "\"section\":\"Exchange\"";
        String jsonResult = result.getResponse().getContentAsString();

        Assertions.assertNotEquals("", jsonResult);
        Assertions.assertFalse(jsonResult.contains(shouldNotContainSection));
        Assertions.assertFalse(jsonResult.contains(shouldNotContainExchangeSection));
    }

    /**
     * Tests failure of getting cards when an invalid sortBy is given
     */
    @Test
    @WithMockUser
    void testGetCardsInvalidSortBy() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/cards?section=ForSale&sortBy=INVALID")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests card is returned in correct order when using sortBy=DATE_ASC
     */
    @Test
    @WithMockUser
    void testCorrectGetCardsSortByDateAsc() throws Exception {

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/cards?section=ForSale&sortBy=DATE_ASC")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        JsonObject convertedObject = new Gson().fromJson(jsonResult, JsonObject.class);
        JsonArray content = convertedObject.get("paginationElements").getAsJsonArray();

        String firstDateString = content.get(0).getAsJsonObject().get("created").toString();
        String secondDateString = content.get(1).getAsJsonObject().get("created").toString();

        LocalDateTime firstDate = LocalDateTime.parse(firstDateString.substring(1, firstDateString.length() - 1));
        LocalDateTime secondDate = LocalDateTime.parse(secondDateString.substring(1, secondDateString.length() - 1));

        Assertions.assertTrue(firstDate.isBefore(secondDate));

    }

    /**
     * Tests card is returned in correct order when using sortBy=DATE_DESC
     */
    @Test
    @WithMockUser
    void testCorrectGetCardsSortByDateDesc() throws Exception {

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/cards?section=ForSale&sortBy=DATE_DESC")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        JsonObject convertedObject = new Gson().fromJson(jsonResult, JsonObject.class);
        JsonArray content = convertedObject.get("paginationElements").getAsJsonArray();

        String firstDateString = content.get(0).getAsJsonObject().get("created").toString();
        String secondDateString = content.get(1).getAsJsonObject().get("created").toString();

        LocalDateTime firstDate = LocalDateTime.parse(firstDateString.substring(1, firstDateString.length() - 1));
        LocalDateTime secondDate = LocalDateTime.parse(secondDateString.substring(1, secondDateString.length() - 1));

        Assertions.assertTrue(secondDate.isBefore(firstDate));
    }

    /**
     * Tests card is returned in correct order when using sortBy=TITLE_AZ
     */
    @Test
    @WithMockUser
    void testCorrectGetCardsSortByTitleAZ() throws Exception {

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/cards?section=ForSale&sortBy=TITLE_AZ")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        JsonObject convertedObject = new Gson().fromJson(jsonResult, JsonObject.class);
        JsonArray content = convertedObject.get("paginationElements").getAsJsonArray();

        String firstTitleRaw = content.get(0).getAsJsonObject().get("title").toString();
        String secondTitleRaw = content.get(1).getAsJsonObject().get("title").toString();

        String firstTitle = firstTitleRaw.substring(1, firstTitleRaw.length() - 1);
        String secondTitle = secondTitleRaw.substring(1, secondTitleRaw.length() - 1);

        Assertions.assertEquals(testCard1.getTitle(), secondTitle);
        Assertions.assertEquals(testCard2.getTitle(), firstTitle);
    }

    /**
     * Tests card is returned in correct order when using sortBy=TITLE_ZA
     */
    @Test
    @WithMockUser
    void testCorrectGetCardsSortByTitleZA() throws Exception {

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/cards?section=ForSale&sortBy=TITLE_ZA")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        JsonObject convertedObject = new Gson().fromJson(jsonResult, JsonObject.class);
        JsonArray content = convertedObject.get("paginationElements").getAsJsonArray();

        String firstTitleRaw = content.get(0).getAsJsonObject().get("title").toString();
        String secondTitleRaw = content.get(1).getAsJsonObject().get("title").toString();

        String firstTitle = firstTitleRaw.substring(1, firstTitleRaw.length() - 1);
        String secondTitle = secondTitleRaw.substring(1, secondTitleRaw.length() - 1);

        Assertions.assertEquals(testCard3.getTitle(), firstTitle);
        Assertions.assertEquals(testCard1.getTitle(), secondTitle);
    }

    /**
     * Tests correct cards returned when a country is specified
     */
    @Test
    @WithMockUser
    void testCorrectGetCardsLocationCountry() throws Exception {

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/cards?section=ForSale&country=TestCountry")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        JsonObject convertedObject = new Gson().fromJson(jsonResult, JsonObject.class);
        JsonArray content = convertedObject.get("paginationElements").getAsJsonArray();

        String countryRaw = content.get(0).getAsJsonObject().getAsJsonObject("creator").getAsJsonObject("homeAddress").get("country").getAsString();
        Assertions.assertEquals(1, content.size());
        Assertions.assertEquals(testCard1.getCreator().getHomeAddress().getCountry(), countryRaw);
    }

    /**
     * Tests correct cards returned when a country and region are specified
     */
    @Test
    @WithMockUser
    void testCorrectGetCardsLocationCountryAndRegion() throws Exception {

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/cards?section=ForSale&country=TestCountry2&region=TestRegion2")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        JsonObject convertedObject = new Gson().fromJson(jsonResult, JsonObject.class);
        JsonArray content = convertedObject.get("paginationElements").getAsJsonArray();

        String countryRaw = content.get(0).getAsJsonObject().getAsJsonObject("creator").getAsJsonObject("homeAddress")
                .get("country").getAsString();
        String regionRaw = content.get(0).getAsJsonObject().getAsJsonObject("creator").getAsJsonObject("homeAddress")
                .get("region").getAsString();

        Assertions.assertEquals(1, content.size());
        Assertions.assertEquals(testCard2.getCreator().getHomeAddress().getCountry(), countryRaw);
        Assertions.assertEquals(testCard2.getCreator().getHomeAddress().getRegion(), regionRaw);
    }

    /**
     * Tests correct cards returned when a country, region, and city are specified
     */
    @Test
    @WithMockUser
    void testCorrectGetCardsLocationCountryAndRegionAndCity() throws Exception {

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/cards?section=ForSale&country=TestCountry&region=TestRegion&city=TestCity")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        JsonObject convertedObject = new Gson().fromJson(jsonResult, JsonObject.class);
        JsonArray content = convertedObject.get("paginationElements").getAsJsonArray();

        String countryRaw = content.get(0).getAsJsonObject().getAsJsonObject("creator").getAsJsonObject("homeAddress")
                .get("country").getAsString();
        String regionRaw = content.get(0).getAsJsonObject().getAsJsonObject("creator").getAsJsonObject("homeAddress")
                .get("region").getAsString();
        String cityRaw = content.get(0).getAsJsonObject().getAsJsonObject("creator").getAsJsonObject("homeAddress")
                .get("city").getAsString();

        Assertions.assertEquals(1, content.size());
        Assertions.assertEquals(testCard1.getCreator().getHomeAddress().getCountry(), countryRaw);
        Assertions.assertEquals(testCard1.getCreator().getHomeAddress().getRegion(), regionRaw);
        Assertions.assertEquals(testCard1.getCreator().getHomeAddress().getCity(), cityRaw);
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
                        .get("/cards?section=ForSale")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JSONObject jsonResult = (JSONObject) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(result.getResponse().getContentAsString());
        Assertions.assertTrue(jsonResult.containsKey("paginationElements"));
        Assertions.assertTrue(jsonResult.containsKey("totalPages"));
        Assertions.assertTrue(jsonResult.containsKey("totalElements"));

    }

    /**
     * Tests extending card display period as an authorized creator user
     */
    @Test
    @WithMockUser
    void testCreatorExtendDisplayPeriod() throws Exception {
        LocalDateTime currentDate = testCard1.getCreated();
        LocalDateTime currentExpiryDate = testCard1.getDisplayPeriodEnd();

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/cards/" + testCard1.getId() + "/extenddisplayperiod")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        testCard1 = cardRepository.getCardById(testCard1.getId());
        Assertions.assertTrue(testCard1.getCreated().isAfter(currentDate));
        Assertions.assertTrue(testCard1.getDisplayPeriodEnd().isAfter(currentExpiryDate));
    }

    /**
     * Tests deleting card as an GAA
     */
    @Test
    @WithMockUser
    void testGlobalAdminExtendDisplayPeriod() throws Exception {
        LocalDateTime currentDate = testCard1.getCreated();
        LocalDateTime currentExpiryDate = testCard1.getDisplayPeriodEnd();

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/cards/" + testCard1.getId() + "/extenddisplayperiod")
                        .cookie(adminCookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        testCard1 = cardRepository.getCardById(testCard1.getId());
        Assertions.assertTrue(testCard1.getCreated().isAfter(currentDate));
        Assertions.assertTrue(testCard1.getDisplayPeriodEnd().isAfter(currentExpiryDate));
    }

    /**
     * Test deleting card if user not logged in
     */
    @Test
    @WithMockUser
    void testUserNotLoggedInExtendDisplayPeriod() throws Exception {
        LocalDateTime currentDate = testCard1.getCreated();
        LocalDateTime currentExpiryDate = testCard1.getDisplayPeriodEnd();

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/cards/" + testCard1.getId() + "/extenddisplayperiod")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        testCard1 = cardRepository.getCardById(testCard1.getId());
        Assertions.assertEquals(currentDate.getDayOfMonth(), testCard1.getCreated().getDayOfMonth());
        Assertions.assertEquals(currentExpiryDate.truncatedTo(ChronoUnit.SECONDS), testCard1.getDisplayPeriodEnd().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @WithMockUser
    void testInvalidUserExtendDisplayPeriod() throws Exception {
        LocalDateTime currentDate = testCard1.getCreated();
        LocalDateTime currentExpiryDate = testCard1.getDisplayPeriodEnd();

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/cards/" + testCard1.getId() + "/extenddisplayperiod")
                        .cookie(cookieInvalid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        testCard1 = cardRepository.getCardById(testCard1.getId());

        Assertions.assertEquals(currentDate.getDayOfMonth(), testCard1.getCreated().getDayOfMonth());
        Assertions.assertEquals(currentExpiryDate.truncatedTo(ChronoUnit.SECONDS), testCard1.getDisplayPeriodEnd().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @WithMockUser
    void testExtendDisplayPeriodNonExistCard() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/cards/" + "12345678912456789" + "/extenddisplayperiod")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }
//***************************************************************************************************

    /**
     * Tests deleting card as an authorized creator user
     */
    @Test
    @WithMockUser
    void testCreatorDeleteCard() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/cards/" + testCard1.getId())
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Tests deleting card as an GAA
     */
    @Test
    @WithMockUser
    void testGlobalAdminDeleteCard() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/cards/" + testCard1.getId())
                        .cookie(adminCookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Test deleting card if user not logged in
     *
     * @throws Exception exception {@link org.springframework.http.HttpStatus#UNAUTHORIZED}
     */
    @Test
    @WithMockUser
    void testUserNotLoggedInDeleteCard() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/cards/" + testCard1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test invalid user is forbidden to delete card
     *
     * @throws Exception exception {@link org.springframework.http.HttpStatus#FORBIDDEN}
     */
    @Test
    @WithMockUser
    void testInvalidUserDeleteCard() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/cards/" + testCard1.getId())
                        .cookie(cookieInvalid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    /**
     * Test a valid user deleting a non-existent card is not acceptable
     *
     * @throws Exception exception {@link org.springframework.http.HttpStatus#NOT_ACCEPTABLE}
     */
    @Test
    @WithMockUser
    void testDeleteNonExistCard() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/cards/" + "12345678912456789")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());

        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/cards/" + "12345678912456789")
                        .cookie(adminCookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    //**********************************************************************************************

    /**
     * Test a non logged in user is not authorized to get a card
     *
     * @throws Exception exception {@link org.springframework.http.HttpStatus#UNAUTHORIZED}
     */
    @Test
    @WithMockUser
    void testUserIsNotLoggedInGetCard() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/cards/" + testCard1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

    }

    /**
     * Test a valid user can get a card
     *
     * @throws Exception exception {@link org.springframework.http.HttpStatus#OK}
     */
    @Test
    @WithMockUser
    void testValidUserGetCard() throws Exception {

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/cards/" + testCard1.getId())
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/cards/" + testCard1.getId())
                        .cookie(adminCookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JSONObject jsonResult = (JSONObject) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(result.getResponse().getContentAsString());
        Assertions.assertTrue(jsonResult.containsKey("id"));
        Assertions.assertTrue(jsonResult.containsKey("creator"));
        Assertions.assertTrue(jsonResult.containsKey("section"));
        Assertions.assertTrue(jsonResult.containsKey("created"));
        Assertions.assertTrue(jsonResult.containsKey("displayPeriodEnd"));
        Assertions.assertTrue(jsonResult.containsKey("title"));
        Assertions.assertTrue(jsonResult.containsKey("description"));
        Assertions.assertTrue(jsonResult.containsKey("keywords"));
    }

    /**
     * Test a user getting a non-existent card is non-acceptable
     *
     * @throws Exception exception {@link org.springframework.http.HttpStatus#NOT_ACCEPTABLE}
     */
    @Test
    @WithMockUser
    void testGetNonExistCard() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/cards/" + "123456789124679981")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable())
                .andReturn();
    }

    //**********************************************************************************************

    /**
     * Test getting card of a valid user if not logged in and invalid session id
     */
    @Test
    @WithAnonymousUser
    void testUserIsNotLoggedInOrInvalidGetUserCard() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + validUser.getId() + "/cards/?section=ForSale")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + validUser.getId() + "/cards/?section=ForSale")
                        .cookie(cookieInvalid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    /**
     * Test getting card of a valid user from a valid user and an admin
     */
    @Test
    @WithMockUser
    void testValidUserOrAdminGetUserCard() throws Exception {

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + validUser.getId() + "/cards/?section=ForSale")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + validUser.getId() + "/cards/?section=ForSale")
                        .cookie(adminCookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JSONObject jsonResult = (JSONObject) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(result.getResponse().getContentAsString());
        Assertions.assertTrue(jsonResult.containsKey("paginationElements"));
        Assertions.assertTrue(jsonResult.containsKey("totalPages"));
        Assertions.assertTrue(jsonResult.containsKey("totalElements"));
    }

    /**
     * Test getting card from a non-exist user from a valid user
     */
    @Test
    @WithMockUser
    void testGetNonExistUserCard() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + "0" + "/cards/?section=ForSale")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable())
                .andReturn();
    }

    //**********************************************************************************************

    @Test
    void testUserNotLoggedInSearchCard() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/cards/search").param("keywords", "Vehicle").param("section", "ForSale")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockUser
    void testUserLoggedInOrAdminSearchCard() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/cards/search").param("keywords", "Vehicle").param("section", "ForSale")
                .cookie(cookieValid)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/cards/search").param("keywords", "Vehicle").param("section", "ForSale")
                .cookie(adminCookie)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }


    //-------------------------------------------Update Card---------------------------------------------------------

    /**
     * Test valid update of card as normal user
     */
    @Test
    @WithMockUser
    void putCardEndpoint_ValidUser_returnsWith200Ok() throws Exception {
        cardRequest.setTitle("New Title");
        cardRequest.setDescription("New Description");

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/cards/" + testCard1.getId())
                        .cookie(cookieValid)
                        .content(objectMapper.writeValueAsString(cardRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Card card = cardRepository.getCardById(testCard1.getId());
        //Test that the cards have the same id, but the title and descriptions are now different
        Assertions.assertEquals(card.getId(), testCard1.getId());
        Assertions.assertNotEquals(card.getTitle(), testCard1.getTitle());
        Assertions.assertNotEquals(card.getDescription(), testCard1.getDescription());
    }

    /**
     * Test valid update of card as a website administrator
     */
    @Test
    @WithMockUser
    void putCardEndpoint_ValidAdmin_returnsWith200Ok() throws Exception {
        cardRequest.setTitle("New Title");
        cardRequest.setDescription("New Description");

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/cards/" + testCard1.getId())
                        .cookie(adminCookie)
                        .content(objectMapper.writeValueAsString(cardRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Card card = cardRepository.getCardById(testCard1.getId());
        //Test that the cards have the same id, but the title and descriptions are now different
        Assertions.assertEquals(card.getId(), testCard1.getId());
        Assertions.assertNotEquals(card.getTitle(), testCard1.getTitle());
        Assertions.assertNotEquals(card.getDescription(), testCard1.getDescription());
    }

    /**
     * Test invalid update of card as a non-logged in user
     */
    @Test
    @WithAnonymousUser
    void putCardEndpoint_InvalidLogin_returnsWith401Unauthorized() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/cards/" + testCard1.getId())
                        .content(objectMapper.writeValueAsString(cardRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test updating a card that does not exist
     */
    @Test
    @WithMockUser
    void putCardEndpoint_CardDoesNotExist_returnsWith404NotFound() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/cards/0")
                        .cookie(adminCookie)
                        .content(objectMapper.writeValueAsString(cardRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Test updating a card the user does not own
     */
    @Test
    @WithMockUser
    void putCardEndpoint_CardNotOwned_returnsWith403Forbidden() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/cards/" + testCard2.getId())
                        .cookie(cookieValid)
                        .content(objectMapper.writeValueAsString(cardRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}
