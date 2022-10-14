package gradle.cucumber.notifications;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.seng302.main.dto.response.NotificationResponse;
import org.seng302.main.helpers.NotificationCategory;
import org.seng302.main.helpers.NotificationTagType;
import org.seng302.main.helpers.NotificationType;
import org.seng302.main.models.Address;
import org.seng302.main.models.Notification;
import org.seng302.main.models.User;
import org.seng302.main.repository.NotificationRepository;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

public class NotificationStepDefs {
    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    NotificationService notificationService;

    Address testAddress1 = new Address("3/24", "Ilam Road", "Christchurch",
            "Canterbury", "New Zealand", "90210");

    User recipient = new User("Juice", "", "Boy", "JB", "", "jb@juice.com",
            LocalDate.now(), "", testAddress1, LocalDate.now(), "ROLE_USER", "password123", "123", null);

    Notification notification;
    List<NotificationResponse> notificationList;

    NotificationTagType existingTag = NotificationTagType.LOW_PRIORITY;
    String updatedTag;

    @Given("I have a notification with an existing tag")
    public void iHaveANotification() {
        userRepository.deleteAll();
        recipient = userRepository.save(recipient);
        notification = new Notification()
                .withMessage("Test Notification, please ignore")
                .withRecipient(recipient)
                .withType(NotificationType.GENERAL)
                .withTag(existingTag);
        notification = notificationRepository.save(notification);

        Assertions.assertNotNull(notificationService.getNotification(notification.getId()));
        Assertions.assertEquals(recipient.getId(), notificationService.getNotification(notification.getId()).getRecipient().getId());
    }

    @When("I add the tag {string}")
    public void iAddTheTag(String tag) {
        updatedTag = tag;
        Assertions.assertDoesNotThrow(() -> notificationService.updateNotificationTag(notification.getId(), recipient, NotificationTagType.fromString(updatedTag)));
    }

    @Then("the notification is updated with that tag")
    public void theNotificationIsUpdatedWithThatTag() {
        Assertions.assertEquals(notificationService.getNotification(notification.getId()).getTagType(), NotificationTagType.fromString(updatedTag));
    }

    @When("I remove the tag")
    public void iRemoveTheTag() {
        Assertions.assertDoesNotThrow(() -> notificationService.updateNotificationTag(notification.getId(), recipient, null));
    }

    @Then("the notification is updated and no longer has a tag")
    public void theNotificationIsUpdatedAndNoLongerHasATag() {
        Assertions.assertNull(notificationService.getNotification(notification.getId()).getTagType());
    }

    @When("I search for that tag")
    public void iSearchForThatTag() {
        notificationList = notificationService.getUserNotification(recipient.getId(), null, existingTag, null);
        Assertions.assertTrue(notificationList.size() > 0);
    }

    @Then("the notifications with that tag are returned")
    public void theNotificationsWithThatTagAreReturned() {
        boolean invalidNotificationFound = false;
        for (NotificationResponse response : notificationList) {
            if (response.getTag() != existingTag) {
                invalidNotificationFound = true;
                break;
            }
        }

        Assertions.assertFalse(invalidNotificationFound);
    }

    @When("I star the notification")
    public void iStarTheNotification() {
        Assertions.assertDoesNotThrow(() -> notificationService.updateNotificationCategory(notification.getId(), recipient, NotificationCategory.STARRED));
    }

    @Then("the notification is starred")
    public void theNotificationIsStarred() {
        Assertions.assertEquals(NotificationCategory.STARRED, notificationService.getNotification(notification.getId()).getCategory());
    }
}
