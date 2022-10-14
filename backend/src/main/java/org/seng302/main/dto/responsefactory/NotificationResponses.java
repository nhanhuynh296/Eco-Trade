package org.seng302.main.dto.responsefactory;

import org.seng302.main.dto.response.NotificationResponse;
import org.seng302.main.models.Notification;
import org.seng302.main.helpers.NotificationType;

import java.util.ArrayList;
import java.util.List;

/**
 * Notification Response factory builder class
 */
public class NotificationResponses {

    /**
     * Add explicit private constructor
     */
    private NotificationResponses() {}

    /**
     * Finish off notification response depending on which type
     *
     * @param notificationResponse NotificationResponse DTO
     * @param notification Notification model
     * @return NotificationResponse
     */
    private static NotificationResponse addFields(NotificationResponse notificationResponse, Notification notification) {
        NotificationType type = notification.getType();
        // Return cardId if it's a CARD_EXPIRING notification
        if (type == NotificationType.CARD_EXPIRING && notification.getCard() != null) {
            notificationResponse
                    .withCardId(notification.getCard().getId());
        }

        // Return KeywordID if it's a KEYWORD_ADDED notification
        else if (type == NotificationType.KEYWORD_ADDED) {
            notificationResponse
                    .withKeywordId(notification.getKeywordId());
        }

        // Return senderId and cardId if it's a COMMENT_RECEIVED notification
        else if (type == NotificationType.COMMENT_RECEIVED) {
            notificationResponse
                    .withSenderId(notification.getSender().getId())
                    .withCardId(notification.getCard().getId());
        }

        else if (type == NotificationType.LIKED && notification.getListing() != null) {
            notificationResponse
                    .withListingId(notification.getListing().getId());
        }

        else if (type == NotificationType.BOUGHT && notification.getSale() != null) {
            notificationResponse
                    .withPrice(notification.getSale().getSoldFor())
                    .withBusinessId(notification.getSale().getBusinessId());
        }

        return notificationResponse;
    }

    /**
     * Creates a single response for notification
     *
     * @param notification instance of Notification
     * @return card response
     */
    public static NotificationResponse getResponse(Notification notification) {
        // These attributes are always present on a notification
        NotificationResponse response = new NotificationResponse()
                .withCreated(notification.getCreated())
                .withId(notification.getId())
                .withReceipient(notification.getRecipient().getId())
                .withMessage(notification.getMessage())
                .withType(notification.getType())
                .withNotificationTag(notification.getTagType())
                .withCategory(notification.getCategory());

        // Add optional attributes to our notification response depending on the type
        return addFields(response, notification);
    }

    /**
     * Creates a list of notification responses
     *
     * @param notifications List of Notification instances
     * @return List of notification responses
     */
    public static List<NotificationResponse> getAllResponses(List<Notification> notifications) {
        List<NotificationResponse> notificationResponses = new ArrayList<>();

        for (Notification notification: notifications) {
            notificationResponses.add(getResponse(notification));
        }

        return notificationResponses;
    }

}
