package org.seng302.main.controllerTests;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.messages.internal.com.google.gson.Gson;
import io.cucumber.messages.internal.com.google.gson.JsonArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.seng302.main.dto.request.MessageRequest;
import org.seng302.main.helpers.NotificationType;
import org.seng302.main.models.*;
import org.seng302.main.repository.CardRepository;
import org.seng302.main.repository.NotificationRepository;
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
import java.util.ArrayList;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;


    private Cookie cookieValid; //Logged in cookie
    private Cookie cookieValid2; //Logged in cookie without notifications
    private Cookie cookieInvalid;

    private User validRecipient;
    private User validRecipient2;
    private User invalidRecipient;

    private Card testCard;

    private Notification testNotification1;
    private Notification testNotification2;
    private Notification testNotification3;


    //Message object
    private final MessageRequest messageRequest = new MessageRequest();

    MvcResult result;


    /**
     * Before each test, ensure that the notification and user repos are cleared. Ensure new creator is saved as well as notification.
     */
    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        //Remove existing users
        notificationRepository.deleteAll();
        cardRepository.deleteAll();
        userRepository.deleteAll();

        validRecipient = new User("Creator", "A", "A", "A", "A", "cja128@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "TestCity", "TestRegion", "TestCountry", ""), LocalDate.now(), "ROLE_USER", "password123", "123", null);
        validRecipient2 = new User("UserB", "", "LastName", "", "No Bio <3", "peepee@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "Route 66", "Not New Zealand", "No Country", ""), LocalDate.now(), "ROLE_USER", "aaaaa", "222", null);
        invalidRecipient = new User("Invalid", "A", "A", "A", "A", "user1@uclive.ac.nz", LocalDate.now(), "", new Address("", "", "", "", "", ""), LocalDate.now(), "ROLE_USER", "password123", null, null);

        validRecipient = userRepository.save(validRecipient);
        validRecipient2 = userRepository.save(validRecipient2);
        invalidRecipient = userRepository.save(invalidRecipient);

        cookieValid = new Cookie("JSESSIONID", "123");
        cookieValid2 = new Cookie("JSESSIONID", "222");
        cookieInvalid = new Cookie("JSESSIONID", "666");

        testCard = new Card(validRecipient, "ForSale", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), "Car", "Subaru", new ArrayList<>());
        testCard = cardRepository.save(testCard);

        testNotification1 = new Notification()
                .withRecipient(validRecipient)
                .withMessage("Your card is about to expire")
                .withType(NotificationType.CARD_EXPIRING)
                .withCreated(LocalDateTime.now());
        testNotification2 = new Notification()
                .withRecipient(validRecipient)
                .withMessage("Your card is about to expire")
                .withType(NotificationType.CARD_EXPIRING)
                .withCreated(LocalDateTime.now());
        testNotification3 = new Notification()
                .withRecipient(validRecipient)
                .withMessage("Your card is about to expire")
                .withType(NotificationType.CARD_EXPIRING)
                .withCreated(LocalDateTime.now());

        testNotification1 = notificationRepository.save(testNotification1);
        testNotification2 = notificationRepository.save(testNotification2);
        testNotification3 = notificationRepository.save(testNotification3);
    }


    //********************************************** GET Notifications ***********************************************//


    /**
     * Test unauthorized get notification for user who is not logged in
     */
    @Test
    void testUserIsNotLoggedInGetNotification() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/notifications")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }


    /**
     * Tests all notifications of a valid user are returned correctly. Should return a list of the users notifications
     */
    @Test
    @WithMockUser
    void testValidGetNotification() throws Exception {

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/notifications")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        JsonArray notifications = new Gson().fromJson(jsonResult, JsonArray.class);
        Assertions.assertEquals(3, notifications.size());
    }


    /**
     * Test empty notification list is returned for user who has no notifications
     */
    @Test
    @WithMockUser
    void testValidGetNotificationReturnEmptyList() throws Exception {
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/notifications")
                        .cookie(cookieValid2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        JsonArray notifications = new Gson().fromJson(jsonResult, JsonArray.class);
        Assertions.assertEquals(0, notifications.size());
    }


    /**
     * Test invalid user get notifications
     */
    @Test
    void testInvalidUserGetNotification() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/notifications")
                        .cookie(cookieInvalid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    /**
     * Test non-existent user gets notifications
     */
    @Test
    void testNonExistentUserGetNotification() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/notifications")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    //********************************************** DELETE Notifications ********************************************//


    /**
     * Test successfully delete notification of a valid recipient
     */
    @Test
    @WithMockUser
    void testValidDeleteNotification() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/notifications/" + testNotification1.getId())
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    /**
     * Test unauthorized deleting notification if user not logged in.
     */
    @Test
    @WithMockUser
    void testUserIsNotLoggedInDeleteNotification() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/notifications/" + testNotification1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    /**
     * Test reject delete notification of notification that doesn't exist
     */
    @Test
    @WithMockUser
    void testDoesntExistDeleteNotification() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/notifications/" + "123123123")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }


    /**
     * Test reject delete notification of an invalid recipient. User requesting deletion of notification does not match
     * the notification recipient
     */
    @Test
    @WithMockUser
    void testInvalidRecipientDeleteNotification() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/notifications/" + testNotification1.getId())
                        .cookie(cookieValid2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
    //********************************************** Restore Notification ********************************************//

    /**
     * Test restore previously deleted notification
     */
    @Test
    @WithMockUser
    void testValidRestore() throws Exception {
        result = this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/notifications/" + testNotification1.getId())
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        int id = Integer.parseInt(result.getResponse().getContentAsString().split(":")[1].split("}")[0]);
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/notifications/" + id + "/restore")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Test 406 not acceptable is returned when notification id does not exist
     */
    @Test
    @WithMockUser
    void testRestoreNotificationInvalidDoesNotExist() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/notifications/" + 50000 + "/restore")
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Test restore previously deleted notification returns 403 forbidden when requester is not notification recipient
     */
    @Test
    @WithMockUser
    void testInvalidRestoreNotRecipient() throws Exception {
        result = this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/notifications/" + testNotification1.getId())
                        .cookie(cookieValid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        int id = Integer.parseInt(result.getResponse().getContentAsString().split(":")[1].split("}")[0]);
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/notifications/" + id + "/restore")
                        .cookie(cookieValid2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    //********************************************** POST Messages ********************************************//

    /**
     * Test create message notification user logged in
     */
    @Test
    @WithMockUser
    void postMessageEndpoint_withValidLogin_returnsWith201Created()throws Exception{
        messageRequest.setCardId(testCard.getId());
        messageRequest.setMessage("You are a very cool guy");
        messageRequest.setRecipientId(testCard.getCreator().getId());

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/notifications/message")
                        .cookie(cookieValid)
                        .content(objectMapper.writeValueAsString(messageRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }
    /**
     * Tests creating new message with the user not logged in
     */
    @Test
    @WithAnonymousUser
    void postMessageEndpoint_withInvalidLogin_returnsWith401Unauthorized() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/notifications/message")
                .content(objectMapper.writeValueAsString(messageRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests creating new message with card that does not exist
     */
    @Test
    @WithMockUser
    void postMessageEndpoint_withNoCardExists_returnsWith400BadRequest() throws Exception {
        messageRequest.setMessage("You are a very cool guy");

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/notifications/message")
                .cookie(cookieValid)
                .content(objectMapper.writeValueAsString(messageRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests creating new message with card that user does not own
     */
    @Test
    @WithMockUser
    void postMessageEndpoint_withCardNotOwned_returnsWith400BadRequest() throws Exception {
        messageRequest.setCardId(testCard.getId());
        messageRequest.setMessage("You are a very cool guy");

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/notifications/message")
                .cookie(cookieValid2)
                .content(objectMapper.writeValueAsString(messageRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
