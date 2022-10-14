package org.seng302.main.controllerTests;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.models.Address;
import org.seng302.main.models.Business;
import org.seng302.main.models.User;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.BusinessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
class BusinessControllerTests {

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

    // Valid address entity for business, each business or user needs seperate address (persistence error occurs)
    Address validAddress1 = new Address("3/24", "Ilam Road", "Christchurch",
            "Canterbury", "New Zealand", "90210");
    Address validAddress2 = new Address("3/25", "Ilam Road", "Christchurch",
            "Canterbury", "New Zealand", "90210");
    Address validAddress3 = new Address("3/26", "Ilam Road", "Christchurch",
            "Canterbury", "New Zealand", "90210");

    // Valid User
    User user = new User("Crab", "", "Rave", "No", "", "crab@rave.com", LocalDate.now(), "", validAddress1, LocalDate.now(), "USER", "password123", "666666", null);
    Cookie userCookie = new Cookie("JSESSIONID", "666666");

    // Invalid address entity
    Address invalidAddressOne = new Address("", "", "", "", "    ", "");
    Address invalidAddressTwo = new Address("", "", "", "", null, "");

    Business testBusinessOne;
    Business testBusinessTwo = new Business(2L, "New World", "Description2", validAddress3, BusinessTypeService.ACCOMMODATION.toString(), localDate);

    // Business Search
    String searchQuery = "?search=name:Lumbridge";

    /**
     * Enter initial objects into database
     */
    @BeforeEach
    public void populateUserAndBusinessTable() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        userRepository.deleteAll();
        user = userRepository.save(user);

        testBusinessOne = new Business(user.getId(), "Lumbridge General Store", "Description1", validAddress2, BusinessTypeService.ACCOMMODATION.toString(), localDate);
        testBusinessOne = businessRepository.save(testBusinessOne);
        businessRepository.save(testBusinessTwo);
    }


    /**
     * Test a valid business can be added into the database if logged in.
     * Also checks it exists in the database.
     */
    @Test
    @WithMockUser
    void testValidBusinessRegister() throws Exception {
        Business testBusinessMisc = new Business(user.getId(), "The Pharmacy", "Description",
                validAddress1, BusinessTypeService.ACCOMMODATION.toString(), localDate);

        // Posts the valid business object to the database
        MvcResult businessResult = this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses")
                        .cookie(userCookie)
                        .content(objectMapper.writeValueAsString(testBusinessMisc))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        // Extracts the business id returned by the successful registry of the business
        int id = Integer.parseInt(businessResult.getResponse().getContentAsString().split(":")[1].split("}")[0]);
        // Checks the business is now in the database
        this.mockMvc.perform(get("/businesses/" + id).cookie(userCookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testBusinessMisc.getName()));
    }


    /**
     * Test a business cannot be registered if the user is not logged in.
     * Checks it does not exist in the database.
     */
    @Test
    void testRegisterWhenLoggedOut() throws Exception {
        long businessId = 3L;
        Business testBusinessMisc = new Business(businessId, "The Pharmacy", "Description", validAddress1, BusinessTypeService.ACCOMMODATION.toString(), localDate);

        // Posts the valid business object to the database but expect unauthorized as they are not logged in
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses")
                        .content(objectMapper.writeValueAsString(testBusinessMisc))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

    }


    /**
     * Requests one specific business and checks if it was the one expected.
     */
    @Test
    @WithMockUser
    void retrieveOneBusinessWithID() throws Exception {
        Long id = testBusinessOne.getId();
        this.mockMvc.perform(get("/businesses/" + id).cookie(userCookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testBusinessOne.getName()));
    }


    /**
     * Requests one specific business and checks it was the one expected.
     */
    @Test
    @WithMockUser
    void retrieveOneBusinessWithInvalidID() throws Exception {
        this.mockMvc.perform(get("/businesses/a"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    /**
     * Requests one specific user, deletes it, then checks if it still exists.
     */
    @Test
    @WithMockUser
    void removeOneBusinessWithID() throws Exception {
        Long id = testBusinessOne.getId();

        this.mockMvc.perform(get("/businesses/" + id).cookie(userCookie))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(delete("/businesses/" + id).cookie(userCookie))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/businesses/" + id).cookie(userCookie))
                .andDo(print())
                // Checks the status sent back is 406
                .andExpect(status().isNotAcceptable());
    }


    /**
     * Tests that a non-logged in user cannot delete a business.
     * Requests one specific user, deletes it, then checks if it still exists.
     */
    @Test
    @WithMockUser
    void testBusinessDeleteWhenLoggedOut() throws Exception {
        Long id = testBusinessOne.getId();

        this.mockMvc.perform(get("/businesses/" + id)
                        .cookie(userCookie))
                .andExpect(status().isOk());

        //Don't give cookie to this request, i.e not logged in
        this.mockMvc.perform(delete("/businesses/" + id))
                .andExpect(status().isUnauthorized());

        this.mockMvc.perform(get("/businesses/" + id)
                        .cookie(userCookie))
                .andExpect(status().isOk());
    }


    /**
     * Attempts to register a business with an invalid: name
     * Should fail to register and return a 400 Bad Request status code.
     */
    @Test
    @WithMockUser
    void testRegisterInvalidBusinessNameUnauthorized() throws Exception {
        long businessId = -1L;
        Business invalidNameBusiness = new Business(
                businessId, "", "Description3",
                validAddress3, BusinessTypeService.ACCOMMODATION.toString(), localDate
        );

        // Posts the valid business object to the database but expect unauthorized as they are not logged in
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses")
                        .content(objectMapper.writeValueAsString(invalidNameBusiness))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        // Checks the business should not exist
        this.mockMvc.perform(get("/businesses/" + businessId).cookie(userCookie))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Attempts to register a business with an invalid: name
     * Should fail to register and return a 400 Bad Request status code.
     */
    @Test
    @WithMockUser
    void testRegisterInvalidBusinessNameAuthorized() throws Exception {
        Business invalidNameBusiness = new Business(
                user.getId(), "", "Description3",
                validAddress3, BusinessTypeService.ACCOMMODATION.toString(), localDate
        );

        // Posts the valid business object to the database
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses")
                        .cookie(userCookie)
                        .content(objectMapper.writeValueAsString(invalidNameBusiness))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /**
     * Attempts to register a business with an invalid: address
     * Should fail to register and return a 400 Bad Request status code.
     */
    @Test
    @WithMockUser
    void testRegisterInvalidBusinessAddress() throws Exception {
        Business invalidNameBusiness = new Business(
                user.getId(), "Valid Name", "Description3",
                null, BusinessTypeService.ACCOMMODATION.toString(), localDate
        );

        // Posts the valid business object to the database
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses")
                        .cookie(userCookie)
                        .content(objectMapper.writeValueAsString(invalidNameBusiness))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /**
     * Attempts to register a business with an invalid: address country blank string
     * Should fail to register and return a 401 unauthorized code.
     */
    @Test
    @WithMockUser
    void testRegisterInvalidBusinessAddressCountryOneUnauthorized() throws Exception {
        long businessId = 3L;
        Business invalidNameBusiness = new Business(
                businessId, "Valid Name", "Description3",
                invalidAddressOne, BusinessTypeService.ACCOMMODATION.toString(), localDate
        );

        // Posts the valid business object to the database but expect unauthorized as they are not logged in
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses")
                        .content(objectMapper.writeValueAsString(invalidNameBusiness))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    /**
     * Attempts to register a business with an invalid: address country blank string
     * Should fail to register and return a 400 Bad Request status code.
     */
    @Test
    @WithMockUser
    void testRegisterInvalidBusinessAddressCountryOneAuthorized() throws Exception {
        Business invalidNameBusiness = new Business(
                user.getId(), "Valid Name", "Description3",
                invalidAddressOne, BusinessTypeService.ACCOMMODATION.toString(), localDate
        );

        // Posts the valid business object to the database but expect bad request
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses")
                        .cookie(userCookie)
                        .content(objectMapper.writeValueAsString(invalidNameBusiness))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /**
     * Attempts to register a business with an invalid: address country null
     * Should fail to register and return a 401 unauthorized code.
     */
    @Test
    @WithMockUser
    void testRegisterInvalidBusinessAddressCountryTwoUnauthorized() throws Exception {
        long businessId = 3L;
        Business invalidNameBusiness = new Business(
                businessId, "Valid Name", "Description3",
                invalidAddressTwo, BusinessTypeService.ACCOMMODATION.toString(), localDate
        );

        // Posts the valid business object to the database but expect unauthorized as they are not logged in
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses")
                        .content(objectMapper.writeValueAsString(invalidNameBusiness))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Attempts to register a business with an invalid: address country null
     * Should fail to register and return a 400 Bad Request code.
     */
    @Test
    @WithMockUser
    void testRegisterInvalidBusinessAddressCountryTwoAuthorized() throws Exception {
        Address validAddress1 = new Address("3/24", "Ilam Road", "Christchurch",
                "Canterbury", "New Zealand", "90210");
        User user = new User("Crab", "", "Rave", "No", "", "crab@rave.com", LocalDate.now(), "", validAddress1, LocalDate.now(), "USER", "password123", "123456789", null);
        Cookie userCookie = new Cookie("JSESSIONID", "123456789");
        user = userRepository.save(user);

        Business invalidNameBusiness = new Business(
                user.getId(), "Valid Name", "Description3",
                invalidAddressTwo, BusinessTypeService.ACCOMMODATION.toString(), localDate
        );

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses")
                        .cookie(userCookie)
                        .content(objectMapper.writeValueAsString(invalidNameBusiness))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /**
     * Attempts to register a business with an invalid: business type
     * Should fail to register and return a 401 unauthorized.
     */
    @Test
    @WithMockUser
    void testRegisterInvalidBusinessTypeUnauthorized() throws Exception {
        long businessId = -2L;
        Business invalidNameBusiness = new Business(
                businessId, "Valid Name", "Description3",
                validAddress3, "Invalid B Type", localDate
        );

        // Posts the valid business object to the database but expect unauthorized as they are not logged in
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses")
                        .content(objectMapper.writeValueAsString(invalidNameBusiness))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        // Checks the business should not exist
        this.mockMvc.perform(get("/businesses/" + businessId).cookie(userCookie))
                .andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Attempts to register a business with an invalid: business type
     * Should fail to register and return a 400 Bad Request status code.
     */
    @Test
    @WithMockUser
    void testRegisterInvalidBusinessTypeAuthorized() throws Exception {
        Address validAddress1 = new Address("3/24", "Ilam Road", "Christchurch",
                "Canterbury", "New Zealand", "90210");
        User user = new User("Crab", "", "Rave", "No", "", "crab@rave.com", LocalDate.now(), "", validAddress1, LocalDate.now(), "USER", "password123", "7777777", null);
        Cookie userCookie = new Cookie("JSESSIONID", "7777777");
        user = userRepository.save(user);
        Business invalidNameBusiness = new Business(
                user.getId(), "Valid Name", "Description3",
                validAddress3, "Invalid B Type", localDate
        );

        // Posts the valid business object to the database
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses")
                        .cookie(userCookie)
                        .content(objectMapper.writeValueAsString(invalidNameBusiness))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /**
     * Attempts to register a business with an invalid: null business type
     * Should fail to register and return a 400 Bad Request status code.
     */
    @Test
    @WithMockUser
    void testRegisterInvalidBusinessTypeNullAuthorized() throws Exception {
        Business invalidNameBusiness = new Business(
                user.getId(), "Valid Name", "Description3",
                validAddress3, null, localDate
        );

        // Posts the valid business object to the database but expect unauthorized as they are not logged in
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/businesses")
                        .cookie(userCookie)
                        .content(objectMapper.writeValueAsString(invalidNameBusiness))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /**
     * Testing searching for business with user not logged in
     */
    @Test
    @WithMockUser
    void testBusinessSearchTypeNotLoggedIn() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/businesses" + searchQuery)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    /**
     * Testing searching for business with valid data
     */
    @Test
    @WithMockUser
    void testBusinessSearchTypeQuery() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/businesses" + searchQuery)
                        .cookie(userCookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}

