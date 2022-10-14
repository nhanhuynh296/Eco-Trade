package org.seng302.main.repository;

import org.seng302.main.helpers.NotificationCategory;
import org.seng302.main.helpers.NotificationTagType;
import org.seng302.main.helpers.NotificationType;
import org.seng302.main.models.Listing;
import org.seng302.main.models.Notification;
import org.seng302.main.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Get all notifications for a specific user
     *
     * @param userId Id of the user
     * @param notificationCategory Notification category
     * @return List of notifications
     */
    Page<Notification> findNotificationsByRecipientIdAndCategoryNotLike(Long userId,
                                                                        NotificationCategory notificationCategory,
                                                                        Pageable pageable);

    /**
     * Get all notifications for a specific user
     *
     * @param userId Id of the user
     * @param notificationType Notification type
     * @param notificationCategory Notification category
     * @return List of notifications
     */
    Page<Notification> findNotificationsByRecipientIdAndTypeAndCategoryNotLike(Long userId,
                                                                               NotificationType notificationType,
                                                                               NotificationCategory notificationCategory,
                                                                               Pageable pageable);

    /**
     * Get all notifications for a specific user
     *
     * @param userId Id of the user
     * @param notificationType Notification type
     * @param notificationTagType Notification tag
     * @param notificationCategory Notification category
     * @return List of notifications
     */
    Page<Notification> findNotificationsByRecipientIdAndTypeAndTagTypeAndCategoryNotLike(Long userId,
                                                                                         NotificationType notificationType,
                                                                                         NotificationTagType notificationTagType,
                                                                                         NotificationCategory notificationCategory,
                                                                                         Pageable pageable);

    /**
     * Get all notifications for a specific user
     *
     * @param userId Id of the user
     * @param notificationTagType Notification tag
     * @param notificationCategory Notification category
     * @return List of notifications
     */
    Page<Notification> findNotificationsByRecipientIdAndTagTypeAndCategoryNotLike(Long userId,
                                                                                  NotificationTagType notificationTagType,
                                                                                  NotificationCategory notificationCategory,
                                                                                  Pageable pageable);


    /**
     * Get all archived notifications for a specific user
     *
     * @param userId Id of the user
     * @param notificationCategory Notification category
     * @return List of notifications
     */
    Page<Notification> findNotificationByRecipientIdAndCategory(Long userId, NotificationCategory notificationCategory, Pageable pageable);

    List<Notification> getNotificationsByRecipient(User user);

    /**
     * Retrieves a singular notification by its unique id
     *
     * @param notificationId unique id of the notification
     * @return One card
     */
    Notification getNotificationById(Long notificationId);

    /**
     * Get notification based off card ID and type of notification
     *
     * @param cardId ID of the card notification is about
     * @param type Type of notification to get
     */
    Notification getByCardIdAndType(Long cardId, NotificationType type);

    /**
     * Delete notification based off card ID and type of notification
     *
     * @param cardId ID of the card notification is about
     * @param type Type of notification to delete
     */
    void deleteNotificationByCardIdAndType(Long cardId, NotificationType type);

    /**
     * Delete all notifications about a specific keyword
     *
     * @param keywordId Specific keyword ID
     */
    void deleteAllByKeywordId(Long keywordId);

    /**
     * Get notification based off listing Id
     *
     * @param listing Specific listing
     */
    List<Notification> getAllByListing(Listing listing);
}
