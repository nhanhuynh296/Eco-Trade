package org.seng302.main.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.seng302.main.helpers.NotificationCategory;
import org.seng302.main.helpers.NotificationTagType;
import org.seng302.main.helpers.NotificationType;
import org.seng302.main.models.Sale;

import java.time.LocalDateTime;

/**
 * NotificationResponse Data Transfer Object
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponse {

    private Long id;
    private Long recipientId;
    private Long senderId;
    private NotificationType type;
    private String message;
    private LocalDateTime expiry;
    private Long cardId;
    private Long keywordId;
    private LocalDateTime created;
    private Long listingId;
    private NotificationTagType tag;
    private NotificationCategory category;

    private Double price;
    private Long businessId;

    /**
     * Id
     *
     * @param id id
     * @return NotificationResponse
     */
    public NotificationResponse withId(long id) {
        this.id = id;
        return this;
    }

    /**
     * SenderId
     *
     * @param id id
     * @return NotificationResponse
     */
    public NotificationResponse withSenderId(long id) {
        this.senderId = id;
        return this;
    }

    /**
     * Recipient
     *
     * @param id id
     * @return NotificationResponse
     */
    public NotificationResponse withReceipient(long id) {
        this.recipientId = id;
        return this;
    }

    /**
     * Type
     *
     * @param type Type
     * @return NotificationResponse
     */
    public NotificationResponse withType(NotificationType type) {
        this.type = type;
        return this;
    }

    /**
     * Message
     *
     * @param message Message
     * @return NotificationResponse
     */
    public NotificationResponse withMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Expiry date
     *
     * @param time Expiry date
     * @return NotificationResponse
     */
    public NotificationResponse withExpiry(LocalDateTime time) {
        this.expiry = time;
        return this;
    }

    /**
     * Card ID
     *
     * @param id id
     * @return NotificationResponse
     */
    public NotificationResponse withCardId(long id) {
        this.cardId = id;
        return this;
    }

    /**
     * Builder for keyword Id
     *
     * @param keywordId keywordId
     * @return NotificationResponse
     */
    public NotificationResponse withKeywordId(long keywordId) {
        this.keywordId = keywordId;
        return this;
    }

    /**
     * Builder for created
     *
     * @param created created time
     * @return NotificationResponse
     */
    public NotificationResponse withCreated(LocalDateTime created) {
        this.created = created;
        return this;
    }

    /**
     * Builder for listing
     *
     * @param listingId ID for listing
     * @return NotificationResponse
     */
    public NotificationResponse withListingId(long listingId) {
        this.listingId = listingId;
        return this;
    }

    /**
     * Builder for tag
     *
     * @param tag Tag of notification
     * @return NotificationResponse
     */
    public NotificationResponse withNotificationTag(NotificationTagType tag) {
        this.tag = tag;
        return this;
    }

    /**
     * Builder for category
     *
     * @param category of the notification
     * @return NotificationResponse
     */
    public NotificationResponse withCategory(NotificationCategory category) {
        this.category = category;
        return this;
    }

    /**
     * Builder for price
     *
     * @param price Price of listing
     * @return NotificationResponse
     */
    public NotificationResponse withPrice(Double price) {
        this.price = price;
        return this;
    }

    /**
     * BusinessId for currency
     *
     * @param businessId of business selling listing
     * @return NotificationResponse
     */
    public NotificationResponse withBusinessId(Long businessId) {
        this.businessId = businessId;
        return this;
    }
}
