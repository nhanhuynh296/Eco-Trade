package org.seng302.main.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.seng302.main.models.Address;
import org.seng302.main.models.Business;
import org.seng302.main.models.Product;
import org.seng302.main.models.User;
import org.seng302.main.repository.BusinessRepository;
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
import javax.transaction.Transactional;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTests {

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
    private ObjectMapper objectMapper;

    private Cookie cookie; //Logged in cookie
    private Cookie adminCookie; //Admin cookie

    // Valid address entity for business, each business or user needs seperate address (persistence error occurs)
    private final Address validAddress1 = new Address("3/24", "Ilam Road", "Christchurch",
            "Canterbury", "New Zealand", "90210");

    private final Address validAddress2 = new Address("3", "Road", "Christchurch",
            "Canterbury", "New Zealand", "90210");

    // Valid business entity for testing product catalogue
    private Business testBusinessOne;
    private Business testBusinessTwo;


    /**
     * Enter initial objects into database
     */
    @BeforeEach
    public void populateBusinessTable() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        //Remove existing
        userRepository.deleteAll();
        businessRepository.deleteAll();

        User user = new User("A", "A", "A", "A", "A", "cja128@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "", "", "", ""), LocalDate.now(), "ROLE_USER", "password123", "55555", null);
        User globalAdminUser = new User("A", "A", "A", "A", "A", "admin@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "", "", "", ""), LocalDate.now(), "ROLE_DEFAULT_ADMIN", "password123", "456", null);

        user = userRepository.save(user);
        userRepository.save(globalAdminUser);

        cookie = new Cookie("JSESSIONID", "55555");
        adminCookie = new Cookie("JSESSIONID", "456");

        testBusinessOne = new Business(user.getId(), "Lumbridge General Store", "Description1", validAddress1, "Accomodation", localDate);
        testBusinessTwo = new Business(11L, "maccas", "Description2", validAddress2, "Accomodation", localDate);
        testBusinessOne = businessRepository.save(testBusinessOne);
        testBusinessTwo = businessRepository.save(testBusinessTwo);
    }

    /**
     * Tests posting a valid product as a unauthorized user
     */
    @Test
    @WithAnonymousUser
    void notLoggedInAddProductTest() throws Exception {

        Product testProduct = new Product(testBusinessOne.getId(), testBusinessOne, "Noodles", "Stringy pasta things", "Ragitonis",
                5.0, localDate);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses/" + testBusinessOne.getId() + "/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests a valid product can be added into a business catalogue
     */
    @Test
    void testValidProductIntoBusiness() throws Exception {

        Product testProduct = new Product(testBusinessOne.getId(), testBusinessOne, "Noodles", "Stringy pasta things", "Ragitonis",
                5.0, localDate);

        //Posts the valid product to the business catalogue
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses/" + testBusinessOne.getId() + "/products")
                        .cookie(cookie)
                        .content(objectMapper.writeValueAsString(testProduct))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    /**
     * Tests a valid product cannot be added into an invalid business catalogue
     */
    @Test
    void testValidProductIntoInvalidBusiness() throws Exception {
        long invalidBusinessID = 420L;
        Product testProduct = new Product(testBusinessOne.getId(), testBusinessOne, "Im going to cry", "No don't hurt me no more", "HELP",
                69.420, localDate);

        //Posts the valid product to the invalid business catalogue
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses/" + invalidBusinessID + "/products")
                        .cookie(cookie)
                        .content(objectMapper.writeValueAsString(testProduct))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests a valid product cannot be added into an invalid business catalogue
     */
    @Test
    void testValidProductIntoValidBusinessGlobalAdmin() throws Exception {
        Product testProduct = new Product(testBusinessOne.getId(), testBusinessOne, "Don't hurt me", "No more", "What is love",
                100d, localDate);

        //Posts the valid product to the valid business catalogue with admin cookie
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses/" + testBusinessOne.getId() + "/products")
                        .cookie(adminCookie)
                        .content(objectMapper.writeValueAsString(testProduct))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    /**
     * Tests an invalid product being added into a business catalogue
     */
    @Test
    void testingNonBusinessAdminAddingProduct() throws Exception {
        Product testProduct = new Product(testBusinessTwo.getId(), testBusinessTwo, "Noodles", "Stringy pasta things", "Ragitonis",
                5.0, localDate);

        //Attempts to post the invalid product into business
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses/" + testBusinessTwo.getId() + "/products")
                        .cookie(cookie)
                        .content(objectMapper.writeValueAsString(testProduct))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    /**
     * {
     * "totalPages": 0,
     * "totalElements": 0
     * "content": [business product data according to section]
     * }
     * Check if Json return in that format
     */
    @Test
    @WithMockUser
    void testGetPaginationInformation() throws Exception {

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/businesses/" + testBusinessOne.getId() + "/products")
                        .cookie(adminCookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JSONObject jsonResult = (JSONObject) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(result.getResponse().getContentAsString());
        Assertions.assertTrue(jsonResult.containsKey("paginationElements"));
        Assertions.assertTrue(jsonResult.containsKey("totalPages"));
        Assertions.assertTrue(jsonResult.containsKey("totalElements"));
    }
}

