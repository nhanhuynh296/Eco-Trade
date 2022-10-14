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
 * The class NotificationCache. Create a table of the same name with attributes as columns.
 * Stores a copy of a recently deleted notification.
 */
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@Getter
@Setter
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class NotificationCache {

    @Id
    @GeneratedValue
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
     * Create a NotificationCache Object from a Notification Object
     * @param notification to convert
     */
    public NotificationCache(Notification notification) {
        recipient = notification.getRecipient();
        card = notification.getCard();
        message = notification.getMessage();
        type = notification.getType();
        listing = notification.getListing();
        created = notification.getCreated();
        expiry = notification.getExpiry();
        keywordId = notification.getKeywordId();
        sender = notification.getSender();
        tagType = notification.getTagType();
        category = notification.getCategory();
        sale = notification.getSale();
    }

}
