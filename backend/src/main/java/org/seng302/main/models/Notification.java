package org.seng302.main.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.seng302.main.helpers.NotificationCategory;
import org.seng302.main.helpers.NotificationTagType;
import org.seng302.main.helpers.NotificationType;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * The class Notification. Create a table of the same name with attributes as columns.
 */
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@Getter
@Setter
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // this field is optional, as notifications could be system-wide?
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "card_id")
    private Card card;

    @Lob
    @Column(name = "message")
    private String message;

    @Column(name = "type")
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @Column(name = "created_on")
    private LocalDateTime created;

    @Column(name = "delete_on")
    private LocalDateTime expiry;

    @Column(name = "keyword_id")
    private Long keywordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(name = "tagged")
    private NotificationTagType tagType = null;

    @Column(name = "category", nullable = false)
    private NotificationCategory category = NotificationCategory.UNREAD;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "sale_id")
    private Sale sale;

    /**
     * Set recipient
     *
     * @param recipient Recipient user
     */
    public Notification withRecipient(User recipient) {
        this.recipient = recipient;
        return this;
    }

    /**
     * Set card
     *
     * @param card Card object
     */
    public Notification withCard(Card card) {
        this.card = card;
        return this;
    }

    /**
     * Set card
     *
     * @param message String message
     */
    public Notification withMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Set card
     *
     * @param type Type of notification
     */
    public Notification withType(NotificationType type) {
        this.type = type;
        return this;
    }

    /**
     * Set card
     *
     * @param created Time notifcation created
     */
    public Notification withCreated(LocalDateTime created) {
        this.created = created;
        return this;
    }

    /**
     * Set notification expiry
     *
     * @param expiry Time of notification expiry
     */
    public Notification withExpiry(LocalDateTime expiry) {
        this.expiry = expiry;
        return this;
    }

    /**
     * Set keywordId
     *
     * @param keywordId Id of keyword
     */
    public Notification withKeywordId(Long keywordId) {
        this.keywordId = keywordId;
        return this;
    }

    /**
     * Set Sender
     *
     * @param sender message notification sender
     */
    public Notification withSender(User sender) {
        this.sender = sender;
        return this;
    }

    /**
     * Set listing
     * @param listing Listing object notification tied to
     */
    public Notification withListing(Listing listing) {
        this.listing = listing;
        return this;
    }

    /**
     * Set tag
     *
     * @param tagType Tag type of this notification, see {TagTypes} enum
     */
    public Notification withTag(NotificationTagType tagType) {
        this.tagType = tagType;
        return this;
    }

    /**
     * Set category of the notification
     *
     * @param category the notification category
     */
    public Notification withCategory(NotificationCategory category) {
        this.category = category;
        return this;
    }

    /**
     * Set sale
     * @param sale Sale object notification tied to
     */
    public Notification withSale(Sale sale) {
        this.sale = sale;
        return this;
    }

}
