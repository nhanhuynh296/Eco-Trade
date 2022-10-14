package org.seng302.main.serviceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.request.MessageRequest;
import org.seng302.main.dto.response.NotificationResponse;
import org.seng302.main.helpers.NotificationCategory;
import org.seng302.main.helpers.NotificationType;
import org.seng302.main.models.*;
import org.seng302.main.repository.CardRepository;
import org.seng302.main.repository.NotificationCacheRepository;
import org.seng302.main.repository.NotificationRepository;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.CardService;
import org.seng302.main.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class NotificationServiceTests {

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

    private User creator = new User("Juice", "", "Boy", "JB", "", "jb@juice.com",
            LocalDate.now(), "", new Address("", "", "", "", "", ""), LocalDate.now(), "ROLE_USER",
            "password123", "123", null);

    private final Card cardNotification = new Card(creator, "ForSale", LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(1), "Car", "Subaru", new ArrayList<>());

    private Notification notification = new Notification();

    private final String section = "ForSale";
    private final LocalDateTime date = LocalDateTime.now();
    private final String title = "Lada";
    private final String description = "Good car";
    private final List<Keyword> keywords = new ArrayList<>();

    @BeforeEach
    public void init() {
        creator = userRepository.save(creator);
        cardRepository.save(cardNotification);

        notification.withRecipient(creator);
        notification = notificationRepository.save(notification);
    }

    @Test
    @Transactional
    void testGetNearExpireCard() {
        LocalDateTime displayPeriodEnd = LocalDateTime.now().plusDays(1).minusHours(1);
        Card card = new Card(creator, section, date, displayPeriodEnd, title, description, keywords);
        Long cardId = cardRepository.save(card).getId();
        cardService.notifyNearExpireCard();
        Card cardAfter = cardRepository.getCardById(cardId);
        Assertions.assertFalse(cardAfter.getNotification().isEmpty());
    }

    /**
     * Checks that the recipient check for a notification is successful
     */
    @Test
    void testValidNotificationRecipientCheck() {
        Notification notification =  new Notification()
                .withRecipient(creator)
                .withMessage("Your card is about to expire")
                .withType(NotificationType.CARD_EXPIRING)
                .withCreated(LocalDateTime.now());
        Notification savedNotification = notificationRepository.save(notification);
        Assertions.assertTrue(notificationService.isNotificationRecipient(creator, savedNotification.getRecipient()));
    }

    /**
     * Checks that the recipient check for a notification is rejected when recipient does not match
     */
    @Test
    void testInvalidNotificationRecipientCheck() {
        Notification notification = new Notification()
                .withRecipient(creator)
                .withMessage("Your card is about to expire")
                .withType(NotificationType.CARD_EXPIRING)
                .withCreated(LocalDateTime.now());
        User fakeCreator = new User("Juice", "", "Boy", "JB", "", "jb@juice.com",
                LocalDate.now(), "", new Address("", "", "", "", "", ""), LocalDate.now(), "ROLE_USER",
                "password123", "123", null);
        userRepository.save(fakeCreator);
        Notification savedNotification = notificationRepository.save(notification);
        Assertions.assertFalse(notificationService.isNotificationRecipient(fakeCreator, savedNotification.getRecipient()));
    }

    /**
     * Checks that the correct notification is deleted given a notification id
     */
    @Test
    void testValidNotificationDelete() {
        Notification notification = new Notification()
                .withRecipient(creator)
                .withMessage("Your card is about to expire")
                .withType(NotificationType.CARD_EXPIRING)
                .withCreated(LocalDateTime.now());
        Notification savedNotification = notificationRepository.save(notification);
        Assertions.assertTrue(notificationRepository.existsById(savedNotification.getId()));

        notificationService.deleteNotification(savedNotification.getId());
        Assertions.assertFalse(notificationRepository.existsById(savedNotification.getId()));
    }

    /**
     * Checks that delete of invalid notification is rejected
     */
    @Test
    void testInvalidNotificationDelete() {
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> notificationService.deleteNotification(123456789L));
    }


    /**
     * Checks create message with no card
     */
    @Test
    void testCreateMessageNoCardThrows() {
        MessageRequest request = new MessageRequest();
        request.setMessage("Hello World!");
        Assertions.assertThrows(ResponseStatusException.class, () -> notificationService.createMessageNotification(request, null, null));
    }

    /**
     * Checks create message returns valid message ID
     */
    @Test
    void testCreateMessageValid() {
        MessageRequest request = new MessageRequest();
        request.setMessage("Hello World!");
        request.setCardId(cardNotification.getId());
        Assertions.assertNotNull(notificationService.createMessageNotification(request, creator, creator));
    }

    /**
     * Checks that by default notifications are unread
     */
    @Test
    void testCreateUnreadMessage() {
        List<NotificationResponse> notificationList = notificationService.getUserNotification(creator.getId(), null, null, null);

        boolean found = false;
        for (NotificationResponse notification : notificationList) {
            if (notification.getCategory() != NotificationCategory.UNREAD) {
                found = true;
                break;
            }
        }

        Assertions.assertFalse(found);
    }

    /**
     * Test cacheNotification returns id of newly cached notification
     */
    @Test
    void testCacheNotification() {
        Notification notification = new Notification()
                .withCreated(LocalDateTime.now())
                .withMessage("you look good today ;)")
                .withRecipient(creator)
                .withSender(creator)
                .withType(NotificationType.COMMENT_RECEIVED);
        notification = notificationRepository.save(notification);
        Long cacheId = notificationService.cacheNotification(notification.getId()).getId();
        NotificationCache notificationCache = notificationCacheRepository.getNotificationCacheById(cacheId);
        Assertions.assertEquals(notification.getMessage(), notificationCache.getMessage());
        Assertions.assertEquals(notification.getCreated().toLocalDate(), notificationCache.getCreated().toLocalDate());
        Assertions.assertEquals(notification.getRecipient().getId(), notificationCache.getRecipient().getId());
        Assertions.assertEquals(notification.getSender().getId(), notificationCache.getSender().getId());
        Assertions.assertEquals(notification.getType(), notificationCache.getType());

    }

    /**
     * Test restoreNotification returns the id of the newly restored notification
     */
    @Test
    void testRestoreNotification() {
        Notification notification = new Notification()
                .withCreated(LocalDateTime.now())
                .withMessage("Hi mum")
                .withRecipient(creator)
                .withSender(creator)
                .withType(NotificationType.COMMENT_RECEIVED);
        notification = notificationRepository.save(notification);
        Long cacheId = notificationService.cacheNotification(notification.getId()).getId();
        Long restoredId = notificationService.restoreNotification(cacheId).getId();
        Assertions.assertNull(notificationCacheRepository.getNotificationCacheById(cacheId));
        Notification newNotification = notificationRepository.getNotificationById(restoredId);
        Assertions.assertEquals(notification.getMessage(), newNotification.getMessage());
        Assertions.assertEquals(notification.getCreated().toLocalDate(), newNotification.getCreated().toLocalDate());
        Assertions.assertEquals(notification.getRecipient().getId(), newNotification.getRecipient().getId());
        Assertions.assertEquals(notification.getSender().getId(), newNotification.getSender().getId());
        Assertions.assertEquals(notification.getType(), newNotification.getType());
    }

    /**
     * Test that starred notifications are returned first then the rest
     */
    @Test
    void testStarredReturnedFirst() {
        Notification notification =  new Notification()
                .withRecipient(creator)
                .withMessage("Your card is about to expire")
                .withType(NotificationType.GENERAL)
                .withCreated(LocalDateTime.now())
                .withCategory(NotificationCategory.STARRED);
        notificationRepository.save(notification);

        notification =  new Notification()
                .withRecipient(creator)
                .withMessage("Your card is about to expire")
                .withType(NotificationType.GENERAL)
                .withCreated(LocalDateTime.now())
                .withCategory(NotificationCategory.UNREAD);
        notificationRepository.save(notification);

        notification =  new Notification()
                .withRecipient(creator)
                .withMessage("Your card is about to expire")
                .withType(NotificationType.GENERAL)
                .withCreated(LocalDateTime.now())
                .withCategory(NotificationCategory.STARRED);
        notificationRepository.save(notification);

        List<NotificationResponse> notificationList = notificationService.getUserNotification(creator.getId(), null, null, null);

        boolean isStarred = true;
        for (NotificationResponse response : notificationList) {
            //Loop through until we find a notification that isn't starred
            if (notification.getCategory() != NotificationCategory.STARRED) {
                isStarred = false;
            } else if (response.getCategory() == NotificationCategory.STARRED && !isStarred) {
                //If we found a starred notification and we're not currently in the starred search
                Assertions.fail();
                break;
            }
        }
    }

    /**
     * Test that only archived notifications are returned
     */
    @Test
    void testOnlyArchivedReturned() {
        Notification notification =  new Notification()
                .withRecipient(creator)
                .withMessage("This will be archived")
                .withType(NotificationType.GENERAL)
                .withCreated(LocalDateTime.now())
                .withCategory(NotificationCategory.UNREAD);
        notificationRepository.save(notification);

        notificationService.updateNotificationCategory(notification.getId(), creator, NotificationCategory.ARCHIVED);

        notification =  new Notification()
                .withRecipient(creator)
                .withMessage("Your card is about to expire")
                .withType(NotificationType.GENERAL)
                .withCreated(LocalDateTime.now())
                .withCategory(NotificationCategory.UNREAD);
        notificationRepository.save(notification);

        List<NotificationResponse> notificationList = notificationService.getUserNotification(creator.getId(),
                null,
                null,
                NotificationCategory.ARCHIVED);

        Assertions.assertEquals(1, notificationList.size());

        for (NotificationResponse response: notificationList) {
            // Loop through response list, if we find a non-archived notification is fails.
            if (response.getCategory() != NotificationCategory.ARCHIVED) {
                Assertions.fail();
                break;
            }
        }
    }

    /**
     * Test that archived notifications are not returned with normal get notifications
     */
    @Test
    void testArchivedNotIncludedInNotificationResponse() {
        Notification notification =  new Notification()
                .withRecipient(creator)
                .withMessage("This will be archived")
                .withType(NotificationType.GENERAL)
                .withCreated(LocalDateTime.now())
                .withCategory(NotificationCategory.UNREAD);
        notificationRepository.save(notification);

        notificationService.updateNotificationCategory(notification.getId(), creator, NotificationCategory.ARCHIVED);

        notification =  new Notification()
                .withRecipient(creator)
                .withMessage("This will be unread")
                .withType(NotificationType.GENERAL)
                .withCreated(LocalDateTime.now())
                .withCategory(NotificationCategory.UNREAD);
        notificationRepository.save(notification);

        // Get all notifications
        List<NotificationResponse> notificationList = notificationService.getUserNotification(creator.getId(),
                null,
                null,
                null);

        for (NotificationResponse response: notificationList) {
            // Loop through response list, if we find an archived notification is fails.
            if (response.getCategory() == NotificationCategory.ARCHIVED) {
                Assertions.fail();
                break;
            }
        }
    }
}
