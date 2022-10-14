package org.seng302.main.services;

import lombok.extern.log4j.Log4j2;
import org.seng302.main.dto.request.MessageRequest;
import org.seng302.main.dto.response.IdResponse;
import org.seng302.main.dto.response.NotificationResponse;
import org.seng302.main.dto.responsefactory.ResponseFactory;
import org.seng302.main.helpers.NotificationCategory;
import org.seng302.main.helpers.NotificationTagType;
import org.seng302.main.helpers.NotificationType;
import org.seng302.main.models.*;
import org.seng302.main.repository.CardRepository;
import org.seng302.main.repository.NotificationCacheRepository;
import org.seng302.main.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Notification Service that communicates with Notification Repo
 */
@Service
@Log4j2
public class NotificationService {

    @Autowired
    CardService cardService;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    NotificationCacheRepository notificationCacheRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    BusinessService businessService;

    private ResponseFactory responseFactory = new ResponseFactory();

    /**
     * Get all user notifications
     *
     * @param userId user id
     * @param notificationType Notification type
     * @param notificationTagType Notification tag
     * @return {@link NotificationResponse}
     */
    public List<NotificationResponse> getUserNotification(Long userId,
                                                          NotificationType notificationType,
                                                          NotificationTagType notificationTagType,
                                                          NotificationCategory notificationCategory) {
        Sort sort = Sort.by(Sort.Order.asc("category"), Sort.Order.desc("created"));
        Pageable pageRequest = PageRequest.of(0, Integer.MAX_VALUE, sort);
        Page<Notification> notificationPage;

        //Query the backend for notifications based on the attributes
        if (notificationCategory == NotificationCategory.ARCHIVED) {
            notificationPage = notificationRepository.findNotificationByRecipientIdAndCategory(userId, notificationCategory, pageRequest);
        } else if (notificationTagType != null && notificationType == null) {
            notificationPage = notificationRepository.findNotificationsByRecipientIdAndTagTypeAndCategoryNotLike(userId, notificationTagType, NotificationCategory.ARCHIVED, pageRequest);
        } else if (notificationTagType == null && notificationType != null) {
            notificationPage = notificationRepository.findNotificationsByRecipientIdAndTypeAndCategoryNotLike(userId, notificationType, NotificationCategory.ARCHIVED,  pageRequest);
        } else if (notificationTagType != null) {
            notificationPage = notificationRepository.findNotificationsByRecipientIdAndTypeAndTagTypeAndCategoryNotLike(userId, notificationType, notificationTagType, NotificationCategory.ARCHIVED, pageRequest);
        } else {
            notificationPage = notificationRepository.findNotificationsByRecipientIdAndCategoryNotLike(userId, NotificationCategory.ARCHIVED, pageRequest);
        }

        return responseFactory.getNotificationResponses(notificationPage.getContent());
    }

    /**
     * Creates a message notification
     *
     * @param messageRequest Message request DTO
     * @param sender Sender of message
     * @return NotificationResponse Response with message ID on success
     */
    public NotificationResponse createMessageNotification(MessageRequest messageRequest, User sender, User recipient) {
        Card card = cardRepository.getCardById(messageRequest.getCardId());
        if (card == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card does not exist");
        }

        /*
         * If the person receiving the message isn't the owner of the card and the sender isn't the card's creator
         * don't allow them to send the message, because then you can ask random users about random cards
         */
        if (!recipient.getId().equals(card.getCreator().getId()) && !sender.getId().equals(card.getCreator().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You or the recipient must be the owner of the card");
        }

        Notification notification = new Notification()
                .withRecipient(recipient)
                .withCard(card)                         // Delete notification on card deletion
                .withMessage(formatMessage(card, sender, messageRequest.getMessage()))
                .withType(NotificationType.COMMENT_RECEIVED)
                .withCreated(LocalDateTime.now())
                .withSender(sender);

        notification = notificationRepository.save(notification);
        return new NotificationResponse().withId(notification.getId());
    }

    /**
     * Add a new notification to every user that has liked the listing when it has been purchased
     *
     * @param listing Listing object to notify all 'likers'
     * @param boughtUser User who bought the listing
     */
    public void notifyAllListingLikers(Listing listing, User boughtUser) {
        ArrayList<Notification> notifications = new ArrayList<>();
        for (User recipient : listing.getLikedUsers()) {
            if (!recipient.getId().equals(boughtUser.getId())) { // Do not notify the buyer if they also liked the listing, see U31 AC1
                Notification notification = new Notification()
                        .withRecipient(recipient)
                        .withMessage(String.format("A listing you liked: '%s', has sold!",
                                listing.getInventoryItem().getProduct().getName()))
                        .withCreated(LocalDateTime.now())
                        .withType(NotificationType.LIKED);
                notifications.add(notification);
            }
        }

        notificationRepository.saveAll(notifications);
    }

    /**
     * Adds a new notification for the user who has bought a listing
     *
     * @param sale The sale entity for a listing
     * @param user The user purchasing the listing
     */
    public void generateBoughtNotification(Sale sale, User user) {

        Business business = businessService.getBusinessById(sale.getBusinessId());

        Notification notification = new Notification()
                .withRecipient(user)
                .withCreated(LocalDateTime.now())
                .withType(NotificationType.BOUGHT)
                .withMessage(getBoughtMessage(sale, business))
                .withSale(sale);

        notificationRepository.save(notification);
    }

    /**
     * Generates and formats the bought notification message. Includes product name, price and business location.
     *
     * @param sale Information about the sale
     * @param business The business selling the listing
     */
    public String getBoughtMessage(Sale sale, Business business) {
        return String.format("Purchase Confirmation: %d %s from %s%nPickup Location: %s",
                sale.getQuantity(),
                sale.getProduct().getName(),
                business.getName(),
                business.getAddress());
    }

    /**
     * Format message to specific message format
     *
     * @param card Card object
     * @param user Sender
     * @param message Sender's message
     * @return Message in a specific format
     */
    private String formatMessage(Card card, User user, String message) {
        return  String.format("Community listing: '%s' %n", card.getTitle()) +
                message.trim().replaceAll("[\\t\\n\\r]+"," ");
    }

    /**
     * Get notification based on notification ID
     *
     * @param id notification id
     * @return A {@link Notification} with corresponding id
     */
    public Notification getNotification(Long id) {
        return notificationRepository.getNotificationById(id);
    }

    /**
     * Get cached notification based on notification ID
     *
     * @param id notification id
     * @return A {@link NotificationCache} with corresponding id
     */
    public NotificationCache getNotificationCache(Long id) {
        return notificationCacheRepository.getNotificationCacheById(id);
    }

    /**
     * Checks if the user attempting to delete the notification is the recipient
     *
     * @param currentUser current user
     * @param recipient   the recipient of the notification
     */
    public boolean isNotificationRecipient(User currentUser, User recipient) {
        return currentUser != null && recipient != null && (
                currentUser.getId().equals(recipient.getId()));
    }

    /**
     * Deletes all notifications in the cache by recipient then creates and saves a new notification in the cache.
     * @param notificationId to cache
     * @return id of newly cached notification (for undo functionality)
     */
    @Transactional
    public IdResponse cacheNotification(Long notificationId) {
        Notification notification = notificationRepository.getNotificationById(notificationId);
        notificationCacheRepository.deleteAllByRecipient(notification.getRecipient());
        NotificationCache notificationCache = new NotificationCache(notification);
        notificationCache = notificationCacheRepository.save(notificationCache);
        IdResponse idResponse = new IdResponse();
        idResponse.setId(notificationCache.getId());
        return idResponse;
    }

    /**
     * Restore a notification from the NotificationCache table to the normal Notification table.
     * Deletes given notification from the cache table.
     * @param notificationCacheId id of notification to restore
     * @return id of newly restored notification
     */
    public IdResponse restoreNotification(Long notificationCacheId) {
        NotificationCache cache = notificationCacheRepository.getNotificationCacheById(notificationCacheId);
        Notification restoredNotification = new Notification()
                .withRecipient(cache.getRecipient())
                .withCard(cache.getCard())
                .withMessage(cache.getMessage())
                .withCreated(cache.getCreated())
                .withExpiry(cache.getExpiry())
                .withSender(cache.getSender())
                .withKeywordId(cache.getKeywordId())
                .withListing(cache.getListing())
                .withTag(cache.getTagType())
                .withCategory(cache.getCategory())
                .withSale(cache.getSale())
                .withType(cache.getType());
        notificationCacheRepository.deleteById(notificationCacheId);
        Long id = (notificationRepository.save(restoredNotification)).getId();
        IdResponse idResponse = new IdResponse();
        idResponse.setId(id);
        return idResponse;
    }

    /**
     * Deletes a notification using notification id
     *
     * @param notificationId the id of the notification to be deleted.
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    /**
     * Deletes all notifications about a specific keyword
     *
     * @param keywordId Delete all notifications with this keywordId.
     */
    public void deleteKeywordNotifications(long keywordId) {
        notificationRepository.deleteAllByKeywordId(keywordId);
    }

    /**
     * Update a notifications tag
     *
     * @param notificationId ID of the notification
     * @param currentUser Current user trying to update the tag
     * @param tag Tag to swap to, nullable if deleting tag
     */
    public void updateNotificationTag(Long notificationId, User currentUser, NotificationTagType tag) {
        Notification notification = validateAndReturnNotificationRequest(notificationId, currentUser);
        notification.withTag(tag);
        notificationRepository.save(notification);
    }

    /**
     * Update a notifications category
     *
     * @param notificationId ID of the notification
     * @param currentUser Current user trying to update the tag
     * @param categoryType Category to swap to
     */
    public void updateNotificationCategory(Long notificationId, User currentUser, NotificationCategory categoryType) {
        Notification notification = validateAndReturnNotificationRequest(notificationId, currentUser);

        // If the notification is getting archived, remove the tag
        if (categoryType == NotificationCategory.ARCHIVED) {
            notification.withTag(null);
        }
        notification.withCategory(categoryType);
        notificationRepository.save(notification);
    }

    private Notification validateAndReturnNotificationRequest(Long notificationId, User currentUser) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);

        if (optionalNotification.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Notification does not exist");
        }

        //We know it's present because of the above isEmpty check
        Notification notification = optionalNotification.get();
        if (!notification.getRecipient().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This notification does not belong to this user");
        }
        return notification;
    }
}
