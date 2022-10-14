package org.seng302.main.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.seng302.main.models.Address;
import org.seng302.main.models.Keyword;
import org.seng302.main.models.User;
import org.seng302.main.repository.CardRepository;
import org.seng302.main.repository.KeywordRepository;
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
import java.util.ArrayList;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
class KeywordControllerTests {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KeywordRepository keywordRepository;

    private final String[] keywords = new String[] {
            "Truck", "Fruit", "Lemon", "Animal",
            "Dog", "Cat", "Truck2", "Towel"
    };
    private ArrayList<Keyword> keywordsSaved = new ArrayList<>();

    private Cookie cookieValid; //Logged in cookie
    private Cookie cookieAdmin;

    private User validUser;
    private User adminUser;

    //Notification Data
    private final JSONObject keywordObject = new JSONObject();
    private final String validName = "Muffins";
    private final String invalidName = "MuffinsMuffinsMuffinsMuffinsMuffinsMuffinsMuffins";



    /**
     * Before each test, ensure that the keyword and user repos are cleared. Ensure new creator is saved as well as keywords.
     */
    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        cardRepository.deleteAll();
        keywordRepository.deleteAll();
        userRepository.deleteAll();

        adminUser = new User("Admin", "A", "A", "A", "A", "tnh33@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "TestCity", "TestRegion", "TestCountry", ""), LocalDate.now(), "ROLE_ADMIN", "password123", "456", null);
        validUser = new User("Creator", "A", "A", "A", "A", "cja128@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "TestCity", "TestRegion", "TestCountry", ""), LocalDate.now(), "ROLE_USER", "password123", "123", null);
        validUser = userRepository.save(validUser);
        adminUser = userRepository.save(adminUser);


        cookieValid = new Cookie("JSESSIONID", "123");
        cookieAdmin = new Cookie("JSESSIONID", "456");


        keywordRepository.deleteAll();
        for (String keyword : keywords) {
            Keyword temp = keywordRepository.save(new Keyword(keyword, LocalDate.now()));
            keywordsSaved.add(temp);
        }
    }

    /**
     * Tests creating new keyword with the user not logged in
     */
    @Test
    @WithAnonymousUser
    void postKeywordEndpoint_withInvalidLogin_returnsWith401Unauthorized() throws Exception {
        keywordObject.put("name", validName);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/keywords")
                        .content(objectMapper.writeValueAsString(keywordObject))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests creating new keyword with the user logged in and valid keyword name
     */
    @Test
    @WithMockUser
    void postKeywordEndpoint_withValidLoginAndName_returnsWith201Created() throws Exception {
        keywordObject.put("name", validName);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/keywords")
                        .cookie(cookieValid)
                        .content(objectMapper.writeValueAsString(keywordObject))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    /**
     * Tests creating new keyword with invalid keyword name
     */
    @Test
    @WithMockUser
    void postKeywordEndpoint_withInvalidName_returnsWith400BadRequest() throws Exception {
        keywordObject.put("name", invalidName);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/keywords")
                        .cookie(cookieValid)
                        .content(objectMapper.writeValueAsString(keywordObject))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests creating new keyword with null keyword name
     */
    @Test
    @WithMockUser
    void postKeywordEndpoint_withNullName_returnsWith400BadRequest() throws Exception {
        keywordObject.put("name", null);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/keywords")
                        .cookie(cookieValid)
                        .content(objectMapper.writeValueAsString(keywordObject))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test getting all keywords (No query)
     */
    @Test
    @WithMockUser
    void testLoggedInQueryAllKeywords() throws Exception {
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                .get("/keywords/search?searchQuery=")
                .cookie(cookieValid)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        Assertions.assertNotEquals("[]", jsonResult);
        Assertions.assertTrue(jsonResult.contains("Lemon"));
        Assertions.assertTrue(jsonResult.contains("Towel"));
        Assertions.assertTrue(jsonResult.contains("Animal"));
    }

    /**
     * Test getting keywords with 't' in name
     */
    @Test
    @WithMockUser
    void testLoggedInQueryTKeywords() throws Exception {
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                .get("/keywords/search?searchQuery=t")
                .cookie(cookieValid)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        Assertions.assertNotEquals("[]", jsonResult);
        Assertions.assertTrue(jsonResult.contains("Cat"));
        Assertions.assertTrue(jsonResult.contains("Towel"));
        Assertions.assertTrue(jsonResult.contains("Truck"));
        Assertions.assertFalse(jsonResult.contains("Dog"));
    }

    /**
     * Test deleting a keyword without logging in
     */
    @Test
    void testNotLoggedInDeleteKeywords() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/keywords/" + keywordsSaved.get(0).getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    /**
     * Test deleting a keyword when logging as an user
     */
    @Test
    void testUserLoggedInDeleteKeywords() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/keywords/" + keywordsSaved.get(0).getId())
                .cookie(cookieValid)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test deleting a keyword when logging as an admin
     */
    @Test
    void testAdminLoggedInDeleteKeywords() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/keywords/" + keywordsSaved.get(0).getId())
                .cookie(cookieAdmin)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }
}
