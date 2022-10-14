package org.seng302.main.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.seng302.main.models.Address;
import org.seng302.main.models.User;
import org.seng302.main.models.UserImage;
import org.seng302.main.repository.UserImageRepository;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    private final LocalDate localDate = LocalDate.now();

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserImageRepository userImageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // User Search
    String searchQuery = "?search=(firstName:*John* OR lastName:*John* OR middleName:*John* OR nickname:*John*)";

    Address dgaaAddress = new Address(null, "There Street", null, null, "New Zealand", null);

    // Only country is required for valid address
    Address invalidAddress = new Address();

    Address validAddress1 = new Address("3/24", "Ilam Road", "Christchurch", "Canterbury", "New Zealand"
            , "90210");
    Address validAddress2 = new Address("3/25", "Ilam Road", "Christchurch", "Canterbury", "New Zealand"
            , "90210");

    User testUserOne = new User("John", "", "Johnson", "Johnny", "empty Bio", "email@here.com.co",
            LocalDate.of(1970, 1, 1), "0200 9020", validAddress1, localDate, "ROLE_USER",
            passwordEncoder.encode("pass"), null, null);

    User updateTestUser = new User("Mary", "", "Mason", "", "Words words words", "username",
            LocalDate.of(1970, 1, 1), "64 00224211", validAddress2, localDate, "ROLE_USER",
            passwordEncoder.encode("password"), "123", null);


    User testDGAA = new User("test", "", "", "", "", "email@hereandnow.com",
            LocalDate.of(1970, 1, 1), "0200 9020", dgaaAddress, localDate, "ROLE_DEFAULT_ADMIN",
            passwordEncoder.encode("pass"), "666666", null);

    Cookie dgaaCookie = new Cookie("JSESSIONID", testDGAA.getSessionTicket());

    Cookie updateUserCookie = new Cookie("JSESSIONID", updateTestUser.getSessionTicket());

    private UserImage testImageOne;


    /**
     * Enters some initial users into the database for testing certain cases, e.g. duplicate emails, etc.
     */
    @BeforeEach
    public void populateUserTable() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Removes existing users
        userRepository.deleteAll();
        userImageRepository.deleteAll();

        userRepository.save(testUserOne);
        updateTestUser = userRepository.save(updateTestUser);
        userRepository.save(testDGAA);

        testImageOne = new UserImage("filename", "thumnailname", testUserOne);
        testImageOne = userImageRepository.save(testImageOne);
    }

    /**
     * Tests a valid user can be registered into the database
     */
    @Test
    @WithMockUser
    void testValidRegister() throws Exception {
        User testUserFive = new User("Abraham", "test", "Linkinpark", "Abe", "ipsumlorem", "emailagain@here.com.co",
                LocalDate.of(1970, 1, 1), "2222 9020", validAddress1, localDate, "ROLE_USER", passwordEncoder.encode("pass"), null, null);
        // Posts the valid user object to the database
        MvcResult userResult = this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(testUserFive))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        // Extracts the user id returned by the successful registry of the user
        int id = Integer.parseInt(userResult.getResponse().getContentAsString().split(":")[1].split("}")[0]);
        // Checks the user is now in the database
        this.mockMvc.perform(get("/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(testUserFive.getFirstName()));
    }

    /**
     * Tests a user with a duplicate email cannot be added to the database - should raise a conflict status
     */
    @Test
    void testDuplicateRegister() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(testUserOne))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }


    /**
     * Tests a user with an null name cannot be registered into the database.
     * If either of the first two tests pass a conflict email will be found.
     */
    @Test
    void testInvalidRegisterNameNull() throws Exception {
        // Test null first name
        User testUserFive = new User(null,
                "TempTwo", "TempThree", "testdummy", "ipsumlorem", "spammail@here.com.co",
                LocalDate.of(1970, 1, 1), "2222 9020", validAddress1, localDate, "ROLE_USER", passwordEncoder.encode("Wordpass"), null, null);
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(testUserFive))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        // Test null last name
        testUserFive.setMiddleName("TempTwo");
        testUserFive.setLastName(null);
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(testUserFive))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        // Test null middle name
        testUserFive.setFirstName("TempOne");
        testUserFive.setMiddleName(null);
        testUserFive.setLastName("TempThree");
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(testUserFive))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }


    /**
     * Tests other required fields (email, date of birth, home address and password) cannot be null.
     */
    @Test
    void testInvalidRegisterNullFields() throws Exception {
        // Test null email
        User testUserFive = new User("TempOne",
                "TempTwo", "TempThree", "testdummy", "ipsumlorem", null,
                LocalDate.of(1970, 1, 1), "2222 9020", validAddress1, localDate, "ROLE_USER", passwordEncoder.encode("Wordpass"), null, null);
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(testUserFive))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        // Test null Date Of Birth
        testUserFive.setEmail("spammail@here.com.co");
        testUserFive.setDateOfBirth(null);
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(testUserFive))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        // Test null address
        testUserFive.setDateOfBirth(LocalDate.of(1970, 1, 1));
        testUserFive.setHomeAddress(invalidAddress);
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(testUserFive))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        // Test null password
        testUserFive.setHomeAddress(validAddress1);
        testUserFive.setPassword(null);
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(testUserFive))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /**
     * Tests excessively long emails are not accepted.
     */
    @Test
    void testInvalidRegisterLongEmail() throws Exception {
        // Test null email
        User testUserFive = new User("TempOne",
                "TempTwo", "TempThree", "testdummy", "ipsumlorem", "spammailaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@here.com.co",
                LocalDate.of(1970, 1, 1), "2222 9020", validAddress1, localDate, "ROLE_USER", passwordEncoder.encode("Wordpass"), null, null);
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(testUserFive))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /**
     * Tests excessively long names are not accepted by the user register.
     */
    @Test
    void testInvalidRegisterLongNames() throws Exception {
        // Test first name of 71 characters
        User testUserFive = new User("TempOneqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq",
                "TempTwo", "TempThree", "testdummy", "ipsumlorem", "spammail@here.com.co",
                LocalDate.of(1970, 1, 1), "2222 9020", validAddress1, localDate, "ROLE_USER", passwordEncoder.encode("Wordpass"), null, null);
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(testUserFive))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        // Test long middle name
        testUserFive.setFirstName("TempOne");
        testUserFive.setMiddleName("TempTwoqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(testUserFive))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        // Test long last name
        testUserFive.setMiddleName("TempTwo");
        testUserFive.setLastName("TempThreeqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(testUserFive))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /**
     * Tests incorrect age are not accepted.
     */
    @Test
    void testInvalidRegisterBadAge() throws Exception {
        // Creates a date that will not be accepted (user must be 13 or greater)
        User testUserFive = new User("TempOne",
                "TempTwo", "TempThree", "testdummy", "ipsumlorem", "spammail@here.com.co",
                LocalDate.of(2015, 2, 12), "2222 9020", validAddress1, localDate, "ROLE_USER", passwordEncoder.encode("Wordpass"), null, null);
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(testUserFive))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /**
     * Tests correct age is accepted.
     */
    @Test
    void testValidRegisterGoodAge() throws Exception {
        // Creates a date that will not be accepted (user must be 13 or greater)
        User testUserFive = new User("TempOne",
                "TempTwo", "TempThree", "testdummy", "ipsumlorem", "spammail@here.com.co",
                LocalDate.of(2007, 2, 12), "2222 9020", validAddress1, localDate, "ROLE_USER", passwordEncoder.encode("Wordpass"), null, null);
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(testUserFive))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }


    /**
     * Tests names with numbers are not accepted.
     */
    @Test
    void testInvalidRegisterBadNames() throws Exception {
        // Test first name
        User testUserFive = new User("T123",
                "TempTwo", "TempThree", "testdummy", "ipsumlorem", "spammail@here.com.co",
                LocalDate.of(1970, 1, 1), "2222 9020", validAddress1, localDate, "ROLE_USER", passwordEncoder.encode("Wordpass"), null, null);
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(testUserFive))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        // Test middle name
        testUserFive.setFirstName("TempOne");
        testUserFive.setMiddleName("123");
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(testUserFive))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        // Test last name
        testUserFive.setMiddleName("TempTwo");
        testUserFive.setLastName("66");
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(testUserFive))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /**
     * Requests one specific user and checks it was the one expected.
     */
    @Test
    @WithMockUser
    void retrieveOneUserWithID() throws Exception {
        Long id = testUserOne.getId();
        this.mockMvc.perform(get("/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(testUserOne.getFirstName()));
    }


    /**
     * Requests one specific user and checks it was the one expected.
     */
    @Test
    @WithMockUser
    void retrieveOneUserWithInvalidID() throws Exception {
        this.mockMvc.perform(get("/users/a"))
                .andExpect(status().isBadRequest());
    }


    /**
     * Tests the login function works with a valid user.
     */
    @Test
    void testUserValidLogin() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/login")
                        .content("{\"email\":\"email@here.com.co\",\"password\":\"pass\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    /**
     * Tests the login function rejects an invalid user - bad password.
     */
    @Test
    void testLoginWithBadPassword() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/login")
                        .content("{\"email\":\"email@here.com.co\",\"password\":\"badpass\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /**
     * Tests the login function rejects an invalid user - bad password.
     */
    @Test
    void testLoginWithBadEmail() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/login")
                        .content("{\"email\":\"notValid@here.com.co\",\"password\":\"pass\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /**
     * Test log in authenticates a user.
     */
    @Test
    void testLoginAuthenticates() throws Exception {
        // Valid user - should succeed
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/login")
                        .content("{\"email\":\"email@here.com.co\",\"password\":\"pass\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(authenticated());            // Tests the user is authenticated here

        // Invalid user email - should fail
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/login")
                        .content("{\"email\":\"INVALID\",\"password\":\"pass\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(unauthenticated());

        // Invalid user password - should fail
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/login")
                        .content("{\"email\":\"email@here.com.co\",\"password\":\"INVALID\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(unauthenticated());
    }

    /**
     * Tests logout functionality, should redirect to "/"
     */
    @Test
    void testLogout() throws Exception {
        this.mockMvc
                .perform(logout())
                .andExpect(status().isOk());
    }


    /**
     * Test if an user who is dgaa is able to make another user to be admin.
     */
    @Test
    @WithMockUser
    void testMakeAdminWithPermission() throws Exception {
        Long id = testUserOne.getId();
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/users/" + id + "/makeadmin")
                        .cookie(dgaaCookie)
                        .with(user(testDGAA.getEmail()).roles("DEFAULT_ADMIN")))
                .andExpect(status().isOk());
    }


    /**
     * Checks a user who is not admin cannot make another user admin.
     */
    @Test
    @WithMockUser
    void testMakeAdminWithoutPermission() throws Exception {
        Long id = testUserOne.getId();
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/users/" + id + "/makeadmin")
                        .with(user(testUserOne.getEmail()).roles("USER")))
                .andExpect(status().isForbidden());
    }


    /**
     * Tests an admin cannot make a non-existent user an admin.
     */
    @Test
    void invalidUserCannotBecomeAdmin() throws Exception {
        Long id = testUserOne.getId();
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/users/" + id + 1000 + "/makeadmin")
                        .cookie(dgaaCookie)
                        .with(user(testDGAA.getEmail()).roles("DEFAULT_ADMIN")))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Testing searching for users with user not logged in
     */
    @Test
    @WithMockUser
    void testBusinessSearchTypeNotLoggedIn() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/users" + searchQuery)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    /**
     * Testing searching for users with valid data
     */
    @Test
    @WithMockUser
    void testBusinessSearchTypeQuery() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/users" + searchQuery)
                        .cookie(dgaaCookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Test a user can update their own data
     * @throws Exception
     */
    @Test
    @WithMockUser
    void testUpdateUserValid() throws Exception {
        User payload = new User("Updated", "Test", "User", "Testy", "Bio", "updatedemail@mail.com",
                LocalDate.of(1970, 1, 1), "2222 9020", validAddress1, localDate, null, "newpass", null, null);

        this.mockMvc.perform(MockMvcRequestBuilders
                .put("/users/" + updateTestUser.getId())
                .cookie(updateUserCookie)
                .content(objectMapper.writeValueAsString(payload))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

        this.mockMvc.perform(get("/users/" + updateTestUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(payload.getFirstName()));

    }

    /**
     * Tests that a GAA (in this case DGAA) can update any user's data (valid data provided).
     * @throws Exception
     */
    @Test
    @WithMockUser
    void testUpdateUserValidGAA() throws Exception {
        User payload = new User("Updated", "Test", "User", "Testy", "Bio", "updatedemail@mail.com",
                LocalDate.of(1970, 1, 1), "2222 9020", validAddress1, localDate, null, passwordEncoder.encode("pass"), null, null);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/" + updateTestUser.getId())
                        .cookie(dgaaCookie)
                        .content(objectMapper.writeValueAsString(payload))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    /**
     * Tests a HTTP Bad Request is sent back upon invalid data.
     * Only an invalid first name is tested as, if this works, you can assume all other validation works (since it
     * has been tested already in the resister tests).
     * @throws Exception
     */
    @Test
    @WithMockUser
    void testUpdateUserInvalidNewData() throws Exception {
        User payload = new User("Updated", "Test", "User", "Testy", "Bio", "updatedemail@mail.com",
                LocalDate.of(1970, 1, 1), "2222 9020", validAddress1, localDate, null, passwordEncoder.encode("pass"), null, null);

        payload.setFirstName("Invalid".repeat(11));
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/" + updateTestUser.getId())
                        .cookie(updateUserCookie)
                        .content(objectMapper.writeValueAsString(payload))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    /**
     * Tests that updating a user that to an already registered email returns a HTTP 409 - Conflict.
     * @throws Exception
     */
    @Test
    @WithMockUser
    void testUpdateUserDuplicateEmail() throws Exception {
        User payload = new User("Updated", "Test", "User", "Testy", "Bio", "updatedemail@mail.com",
                LocalDate.of(1970, 1, 1), "2222 9020", validAddress1, localDate, null, passwordEncoder.encode("pass"), null, null);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/" + updateTestUser.getId())
                        .cookie(updateUserCookie)
                        .content(objectMapper.writeValueAsString(payload))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/" + testUserOne.getId())
                        .cookie(dgaaCookie)
                        .content(objectMapper.writeValueAsString(payload))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

    }

    /**
     * Tests that updating a user as a different, non-GAA, user returns a 403 - Forbidden.
     * @throws Exception
     */
    @Test
    @WithMockUser
    void testUpdateUserForbidden() throws Exception {
        User payload = new User("Updated", "Test", "User", "Testy", "Bio", "updatedemail@mail.com",
                LocalDate.of(1970, 1, 1), "2222 9020", validAddress1, localDate, null, passwordEncoder.encode("pass"), null, null);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/" + testUserOne.getId())
                        .cookie(updateUserCookie)
                        .content(objectMapper.writeValueAsString(payload))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that an existing image can be made a primary image for a real user.
     */
    @Test
    @WithMockUser
    void testMakePrimaryImage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/images/" + testImageOne.getId() + "/makeprimary")
                        .cookie(updateUserCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Tests that a non-existing image cannot be made a primary image for a real user.
     */
    @Test
    @WithMockUser
    void testMakePrimaryImageNonExistingImage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/images/" + testImageOne.getId()+100 + "/makeprimary")
                        .cookie(updateUserCookie)
                        .with(user(updateTestUser.getEmail()).roles("USER")))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Tests a non-logged in user cannot change a primary image.
     */
    @Test
    @WithMockUser
    void testMakePrimaryImageBadUser() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/images/" + testImageOne.getId()+100 + "/makeprimary"))
                .andExpect(status().isUnauthorized());
    }

}


