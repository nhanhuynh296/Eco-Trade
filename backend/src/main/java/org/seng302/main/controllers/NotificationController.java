package org.seng302.main.controllers;

import lombok.extern.log4j.Log4j2;
import org.seng302.main.dto.request.MessageRequest;
import org.seng302.main.dto.response.IdResponse;
import org.seng302.main.dto.response.NotificationResponse;
import org.seng302.main.helpers.NotificationCategory;
import org.seng302.main.helpers.ControllerHelper;
import org.seng302.main.helpers.NotificationTagType;
import org.seng302.main.models.Notification;
import org.seng302.main.models.NotificationCache;
import org.seng302.main.helpers.NotificationType;
import org.seng302.main.models.User;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.NotificationService;
import org.seng302.main.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Notification Controller
 *
 * Handles all the responses
 */
@Controller
@Log4j2
@RequestMapping()
public class NotificationController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    ControllerHelper controllerHelper;

    /**
     * Get user all notification, only a user can query his own {@link org.seng302.main.models.Notification notification}
     *
     * @param sessionId session id
     * @return 200 OK response code with JSON formatted notifications
     * 401 Unauthorized
     * 406 Not acceptable
     */
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponse>> getAllNotificationsForUser(@RequestParam(name = "notificationType", required = false) NotificationType notificationType,
                                                                                 @RequestParam(name = "notificationTag", required = false) NotificationTagType notificationTagType,
                                                                                 @RequestParam(name = "notificationCategory", required = false) NotificationCategory notificationCategory,
                                                                                 @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info("[GET /notifications] Get user notifications");

        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        if (sessionId.equals("None") || currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "A user has to be logged in to make this request.");
        }

        return new ResponseEntity<>(notificationService.getUserNotification(currentUser.getId(), notificationType, notificationTagType, notificationCategory), HttpStatus.OK);
    }

    /**
     * Deletes a user notification. Only the recipient of the notification can delete the notification. Will not stop the card
     * from being removed from the marketplace if it is a near expiry notification.
     *
     * @param notificationId id of the notification to delete
     * @param sessionId session of the user performing the delete
     * @return 200 Ok, the notification was successfully deleted
     * 401 Unauthorized, invalid access token
     * 403 Forbidden, user performing delete is not the recipient
     * 406 Not acceptable
     */
    @DeleteMapping("/notifications/{notificationId}")
    public ResponseEntity<IdResponse> deleteNotification(@PathVariable(name = "notificationId") Long notificationId,
                                                         @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[DELETE /notification] Trying to delete a notification with ID %s.", notificationId));
        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        Notification notification = notificationService.getNotification(notificationId);

        if (sessionId.equals("None") || currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request.");
        }

        if (notification == null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Notification does not exist");
        }

        if (!notificationService.isNotificationRecipient(currentUser, notification.getRecipient())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete this notification");
        }

        log.info(String.format("Caching notification with ID %s.", notificationId));
        IdResponse cacheId = notificationService.cacheNotification(notificationId);
        notificationService.deleteNotification(notificationId);

        return new ResponseEntity<>(cacheId, HttpStatus.OK);
    }

    /**
     * Restores a user's notification. Only the recipient of the notification can restore the notification.
     *
     * @param notificationCacheId id of the notification to restore
     * @param sessionId session of the user performing the restoration
     * @return 200 Ok and id of restored notification if the notification was successfully restored
     * 401 Unauthorized, invalid access token
     * 403 Forbidden, user performing delete is not the recipient
     * 406 Not acceptable
     */
    @PutMapping("/notifications/{notificationCacheId}/restore")
    public ResponseEntity<IdResponse> restoreNotification(@PathVariable(name = "notificationCacheId") Long notificationCacheId,
                                                          @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[PUT /notifications/{notificationCacheId}/restore] " +
                "Trying to restore a deleted notification with ID %s.", notificationCacheId));

        NotificationCache notificationCache = notificationService.getNotificationCache(notificationCacheId);

        User currentUser = controllerHelper.authorizeUser(sessionId);

        if (notificationCache == null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Notification does not exist");
        }

        if (!notificationService.isNotificationRecipient(currentUser, notificationCache.getRecipient())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to restore this notification");
        }

        IdResponse idResponse = notificationService.restoreNotification(notificationCacheId);
        return new ResponseEntity<>(idResponse, HttpStatus.OK);

    }

    /**
     * Creates a message notification with a link to the relevant card. Sends to the card owner.
     *
     * @param request Message request DTO
     * @param sessionId session of the user
     * @return 201 Created, the notification was successfully created
     * 400 Bad Request, invalid card
     * 401 Unauthorized, invalid access token
     */
    @PostMapping("/notifications/message")
    public ResponseEntity<NotificationResponse> sendMessage(@RequestBody MessageRequest request,
                                                 @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[POST /notifications/message] Trying to post a new message for card ID %d", request.getCardId()));

        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        if (sessionId.equals("None") || currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request.");
        }

        User recipient = userRepository.findUserById(request.getRecipientId());
        if (recipient == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid recipient of message");
        }

        return new ResponseEntity<>(notificationService.createMessageNotification(request, currentUser, recipient), HttpStatus.CREATED);
    }

    /**
     * Update an existing notifications tag to one of the "TagTypes" options
     *
     * @param notificationId notificationId
     * @param tagType Type of tag to apply to this notification, nullable
     * @param sessionId Session of the user
     * @return 200 OK if tag applied successfully
     *  400 Bad request, notification non existent
     *  401 Unauthorized, No token
     *  403 Forbidden, Notification doesn't belong to you
     */
    @PutMapping("/notifications/{notificationId}/tag")
    public ResponseEntity<HttpStatus> updateNotificationTag(@PathVariable(name = "notificationId") Long notificationId,
                                                            @RequestBody NotificationTagType tagType,
                                                            @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId)
    {
        log.info(String.format("[PUT /notifications/{notificationId}/tag] Trying to update tag with notification ID of: %d", notificationId));

        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        if (sessionId.equals("None") || currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "A user has to be logged in to make this request.");
        }

        notificationService.updateNotificationTag(notificationId, currentUser, tagType);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Update an existing notifications category to one of the "NotificationCategory" options
     *
     * @param notificationId notificationId
     * @param categoryType Type of category to apply to this notification, non nullable
     * @param sessionId Session of the user
     * @return 200 OK if category changed successfully
     *  400 Bad request, notification non existent
     *  401 Unauthorized, No token
     *  403 Forbidden, Notification doesn't belong to you
     */
    @PutMapping("/notifications/{notificationId}/category")
    public ResponseEntity<HttpStatus> updateNotificationCategory(@PathVariable(name = "notificationId") Long notificationId,
                                                                 @RequestBody NotificationCategory categoryType,
                                                                 @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId)
    {
        log.info(String.format("[PUT /notifications/{notificationId}/category] Trying to update category with notification ID of: %d", notificationId));

        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        if (sessionId.equals("None") || currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "A user has to be logged in to make this request.");
        }

        notificationService.updateNotificationCategory(notificationId, currentUser, categoryType);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
