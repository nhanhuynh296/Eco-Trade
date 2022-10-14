package org.seng302.main.services;

import lombok.extern.log4j.Log4j2;
import org.seng302.main.helpers.NotificationType;
import org.seng302.main.models.Listing;
import org.seng302.main.models.Notification;
import org.seng302.main.models.User;
import org.seng302.main.repository.ListingRepository;
import org.seng302.main.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Log4j2
public class LikesService {

    @Autowired
    ListingRepository listingRepository;

    @Autowired
    NotificationRepository notificationRepository;

    /**
     * Adds the user to the set of users who like a listing
     *
     * @param listingId - of the listing being liked/unliked
     * @param user - the user that has liked or disliked the listing
     */
    @Transactional
    public void addLikeToListing(long listingId, User user) {
        Listing listing = getListing(listingId);
        if (listing.hasUserLikedListing(user)) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "The user already likes this listing.");
        }
        listing.addLikedUser(user);
        listingRepository.save(listing);

        Notification notification = getListingInteractNotification(listing, user, true);
        notificationRepository.save(notification);
    }

    /**
     * Removes the user from the set of users who like a listing
     *
     * @param listingId - of the listing being liked/unliked
     * @param user - the user that has liked or disliked the listing
     */
    @Transactional
    public void removeLikeFromListing(long listingId, User user) {
        Listing listing = getListing(listingId);
        if (!listing.hasUserLikedListing(user)) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "The user already doesn't like this listing.");
        }
        Notification notification = getListingInteractNotification(listing, user, false);

        listing.removeLikedUser(user);
        listingRepository.save(listing);
        notificationRepository.save(notification);
    }

    /**
     * Helper function to retrieve a listing based on its id
     *
     * @param id - of the listing
     * @return - the listing
     */
    public Listing getListing(Long id) {
        Listing listing = listingRepository.getListingById(id);
        if (listing == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Listing does not exist!");
        } else {
            return listing;
        }
    }

    /**
     * Generate a notification for a user liking/unliking a notification
     *
     * @param listing - the listing being liked/unliked
     * @param user - the user liking/unliking
     * @param liking - true if user liking, false if user unliking
     * @return the new notification
     */
    @Transactional
    public Notification getListingInteractNotification(Listing listing, User user, boolean liking) {
        Notification listingNotification = new Notification()
                .withRecipient(user)
                .withCreated(LocalDateTime.now())
                .withListing(listing);

        removePreviousNotification(listing, user);
        listingNotification.withType(NotificationType.LIKED);

        if (liking) {
            listingNotification
                    .withMessage(String.format("You liked a listing: '%s'", listing.getInventoryItem().getProduct().getName()));
        } else {
            listingNotification
                    .withMessage(String.format("You unliked a listing: '%s'", listing.getInventoryItem().getProduct().getName()));
        }

        return listingNotification;
    }

    /**
     * Checks if the user already has a notification associated with the listing, removes the notification if it exists.
     *
     * @param listing - the listing being liked
     * @param user - user interacting with the listing
     */
    @Transactional
    public void removePreviousNotification(Listing listing, User user) {

        List<Notification> notifications = notificationRepository.getAllByListing(listing);

        for (Notification notification : notifications) {
            if (notification.getRecipient() == user && notification.getType() == NotificationType.LIKED) {
                notificationRepository.delete(notification);
            }
        }
    }

}
