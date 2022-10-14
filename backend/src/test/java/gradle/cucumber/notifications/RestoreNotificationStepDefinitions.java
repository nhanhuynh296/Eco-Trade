package gradle.cucumber.notifications;

import gradle.cucumber.SpringIntegrationTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.seng302.main.helpers.NotificationType;
import org.seng302.main.models.Address;
import org.seng302.main.models.Notification;
import org.seng302.main.models.User;
import org.seng302.main.repository.CardRepository;
import org.seng302.main.repository.NotificationCacheRepository;
import org.seng302.main.repository.NotificationRepository;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.CardService;
import org.seng302.main.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RestoreNotificationStepDefinitions extends SpringIntegrationTest {

    @Autowired
    CardService cardService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    NotificationCacheRepository notificationCacheRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationRepository notificationRepository;

    private final User creator = new User("Juice", "", "Boy", "JB", "", "jb@juice.com",
            LocalDate.now(), "", new Address("", "", "", "", "", ""), LocalDate.now(), "ROLE_USER",
            "password123", "123", null);

    Notification notification;
    Long cacheId;
    Long restoredId;

    @Given("I am logged in and have just deleted a notification")
    public void i_am_logged_in_and_have_just_deleted_a_notification() {
        userRepository.save(creator);
        notification = new Notification()
                .withCreated(LocalDateTime.now())
                .withMessage("Why are still here?")
                .withRecipient(creator)
                .withSender(creator)
                .withType(NotificationType.COMMENT_RECEIVED);
        notification = notificationRepository.save(notification);
        cacheId = notificationService.cacheNotification(notification.getId()).getId();
        notificationService.deleteNotification(notification.getId());
    }

    @When("I click undo")
    public void i_click_undo() {
        restoredId = notificationService.restoreNotification(cacheId).getId();
    }

    @Then("the notification is restored")
    public void the_notification_is_restored() {
        Assertions.assertNull(notificationCacheRepository.getNotificationCacheById(cacheId));
        Notification newNotification = notificationRepository.getNotificationById(restoredId);
        Assertions.assertEquals(notification.getMessage(), newNotification.getMessage());
    }

}
