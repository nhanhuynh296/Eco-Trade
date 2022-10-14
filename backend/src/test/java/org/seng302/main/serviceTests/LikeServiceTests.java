package org.seng302.main.serviceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.helpers.NotificationType;
import org.seng302.main.models.*;
import org.seng302.main.repository.*;
import org.seng302.main.services.LikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@SpringBootTest
@Transactional
class LikeServiceTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private LikesService likesService;

    private Listing listing;
    private User user;

    /**
     * Set up the testing database entities
     */
    @BeforeEach
    public void init() {
        Address address = new Address("1", "Nowhere", "Varrock", "Tirannwn", "Gilenor", "800");
        user = new User("A", "A", "A", "A", "A", "cja128@uclive.ac.nz",
                LocalDate.now(), "", address, LocalDate.now(), "ROLE_USER", "password123", "55555", null);
        user = userRepository.save(user);

        Address address2 = new Address("5", "Somewhere", "Over", "The", "Rainbow", "800");
        Business testBusiness = new Business(user.getId(), "Lumbridge General Store", "Description1", address2,
                "Accomodation", LocalDate.now());
        testBusiness = businessRepository.save(testBusiness);

        Product testProduct = new Product(testBusiness.getId(), testBusiness, "Watties Baked Beans", "Baked beans in a can",
                "Heinz Wattie's Limited", 2.2, LocalDate.now());
        testProduct = productRepository.save(testProduct);

        InventoryItem testInventory = new InventoryItem(testProduct, 69, 10.0, 100.0,
                LocalDate.now(), null, null, LocalDate.now().plusDays(20));
        testInventory = inventoryRepository.save(testInventory);

        listing = new Listing(testInventory, 2, 4.0, "Beans",
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(5));
        listing = listingRepository.save(listing);
    }

    /**
     * Test that a user can like a listing
     */
    @Test
    void testAddLike() {
        likesService.addLikeToListing(listing.getId(), user);
        listing = likesService.getListing(listing.getId());
        Assertions.assertEquals(1, listing.getLikedUsers().size());
    }

    /**
     * Test that a user cannot like a listing it has already liked
     */
    @Test
    void testAddLikeToLikedListing() {
        try {
            likesService.addLikeToListing(listing.getId(), user);
            likesService.addLikeToListing(listing.getId(), user);
        } catch (ResponseStatusException e) {
            Assertions.assertEquals(HttpStatus.METHOD_NOT_ALLOWED, e.getStatus());
        }
    }

    /**
     * Test that a user can unlike a listing
     */
    @Test
    void testRemoveLike() {
        likesService.addLikeToListing(listing.getId(), user);
        likesService.removeLikeFromListing(listing.getId(), user);
        listing = likesService.getListing(listing.getId());
        Assertions.assertEquals(0, listing.getLikedUsers().size());
    }

    /**
     * Test that a user cannot unlike a listing already doesn't like
     */
    @Test
    void testRemoveLikeFromNotLikedListing() {
        try {
            likesService.removeLikeFromListing(listing.getId(), user);
        } catch (ResponseStatusException e) {
            Assertions.assertEquals(HttpStatus.METHOD_NOT_ALLOWED, e.getStatus());
        }
    }

    /**
     * Test correct notification is generated for liking a listing
     */
    @Test
    void testCorrectNotificationLikingListing() {
        likesService.addLikeToListing(listing.getId(), user);

        Notification notification = notificationRepository.getNotificationsByRecipient(user).get(0);

        Assertions.assertEquals(NotificationType.LIKED, notification.getType());
        Assertions.assertEquals(String.format("You liked a listing: '%s'", listing.getInventoryItem().getProduct().getName()),
                                    notification.getMessage());
    }

    /**
     * Test correct notification is generated for unliking a listing
     */
    @Test
    void testCorrectNotificationUnlikingListing() {
        likesService.addLikeToListing(listing.getId(), user);
        likesService.removeLikeFromListing(listing.getId(), user);

        Notification notification = notificationRepository.getNotificationsByRecipient(user).get(0);

        Assertions.assertEquals(String.format("You unliked a listing: '%s'", listing.getInventoryItem().getProduct().getName()),
                notification.getMessage());
    }



    /**
     * Test previous liking notification is removed when user unliked the notification
     */
    @Test
    void testNotificationRemovedLikedListing() {
        likesService.addLikeToListing(listing.getId(), user);

        //Check that both the listing and the user each have one like/notification
        Assertions.assertEquals(1, listing.getLikedUsers().size());
        Assertions.assertEquals(1, notificationRepository.getNotificationsByRecipient(user).size());

        likesService.removeLikeFromListing(listing.getId(), user);

        //Check that the listing likes decrease but the user still has one notification, the unliking notification
        Assertions.assertEquals(0, listing.getLikedUsers().size());
        Assertions.assertEquals(1, notificationRepository.getNotificationsByRecipient(user).size());

    }

}

