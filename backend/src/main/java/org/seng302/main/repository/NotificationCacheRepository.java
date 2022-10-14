package org.seng302.main.repository;

import org.seng302.main.models.NotificationCache;
import org.seng302.main.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationCacheRepository  extends JpaRepository<NotificationCache, Long> {

    /**
     * Delete all notifications with a given recipient
     * @param recipient to delete by
     */
    void deleteAllByRecipient(User recipient);

    /**
     * Get a cached notification by its id
     * @param id of notification
     * @return NotificationCache Object
     */
    NotificationCache getNotificationCacheById(Long id);
}
