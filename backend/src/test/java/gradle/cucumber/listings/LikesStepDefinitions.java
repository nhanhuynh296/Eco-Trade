package gradle.cucumber.listings;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.seng302.main.helpers.NotificationType;
import org.seng302.main.models.*;
import org.seng302.main.repository.*;
import org.seng302.main.services.LikesService;
import org.seng302.main.services.ListingService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Transactional
public class LikesStepDefinitions {
    @Autowired
    UserRepository userRepository;

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    ListingService listingService;

    @Autowired
    ListingRepository listingRepository;

    @Autowired
    LikesService likesService;

    @Autowired
    NotificationRepository notificationRepository;

    LocalDate date = LocalDate.of(2020, 12, 11);
    Business business;
    Address address = new Address("3/24", "Ilam Road", "Christchurch", "Canterbury", "New Zealand", "90210");

    User user = new User("Juice", "", "Boy", "JB", "", "jb@juice.com",
                    LocalDate.now(), "", address, LocalDate.now(), "ROLE_USER", "password123", "123", null);
    User userTwo = new User("Apple", "", "Girl", "AG", "", "ag@apple.com",
            LocalDate.now(), "", address, LocalDate.now(), "ROLE_USER", "notmypassword", "345", null);
    Product product;
    InventoryItem inventoryItem;
    Listing listing;

    @Given("The user does not like a listing")
    public void theUserDoesNotLikeAListing() {
        businessRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(user);

        business = new Business(user.getId(), "Business", "cool business", address,
                "Accommodation", LocalDate.now());
        business = businessRepository.save(business);

        product = productRepository.save(new Product(business.getId(), business, "Apple", "Apple is red",
                "Apple Company", 1.0, date));
        product = productRepository.save(product);

        inventoryItem = new InventoryItem(product, 10, 1.5d, 250d, null, null, null, LocalDate.now());
        inventoryItem = inventoryRepository.save(inventoryItem);

        listing = new Listing(inventoryItem, 10, 10d, null, date, date);
        listing = listingRepository.save(listing);

        Assertions.assertFalse(listing.getLikedUsers().contains(user));
    }

    @When("A listing is liked by the user")
    public void aListingIsLikedByTheUser() {
        likesService.addLikeToListing(listing.getId(), user);
        listing = listingRepository.getListingById(listing.getId());
        Assertions.assertTrue(listing.getLikedUsers().size() > 0);
    }

    @Then("The user likes the listing")
    public void theUserLikesTheListing() {
        Assertions.assertTrue(user.getLikedListings().contains(listing));
    }

    @Given("The user likes a listing")
    public void theUserLikesAListing() {
        businessRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(user);

        business = new Business(user.getId(), "Business", "cool business", address,
                "Accommodation", LocalDate.now());
        business = businessRepository.save(business);

        product = productRepository.save(new Product(business.getId(), business, "Apple", "Apple is red",
                "Apple Company", 1.0, date));
        product = productRepository.save(product);

        inventoryItem = new InventoryItem(product, 10, 1.5d, 250d, null, null, null, LocalDate.now());
        inventoryItem = inventoryRepository.save(inventoryItem);

        listing = new Listing(inventoryItem, 10, 10d, null, date, date);
        listing = listingRepository.save(listing);

        likesService.addLikeToListing(listing.getId(), user);
        Assertions.assertTrue(listing.hasUserLikedListing(user));
    }

    @When("A listing is unliked by the user")
    public void aListingIsUnlikedByTheUser() {
        int numLikes = listingRepository.getListingById(listing.getId()).getLikedUsers().size();
        user = userRepository.save(user);
        likesService.removeLikeFromListing(listing.getId(), user);
        listing = listingRepository.getListingById(listing.getId());
        Assertions.assertEquals(listing.getLikedUsers().size(), numLikes - 1);
    }

    @Then("The user does not like the listing")
    public void theUserDoesNotLikeTheListing() {
        Assertions.assertFalse(listing.getLikedUsers().contains(user));
    }

    @Then("I should have a message added to my home feed")
    public void iShouldHaveAMessageAddedToMyHomeFeed() {
        boolean receivedNotification = false;
        List<Notification> notifications = notificationRepository.getNotificationsByRecipient(user);
        for (Notification notification : notifications) {
            if (notification.getType() == NotificationType.LIKED && notification.getListing().getId().equals(listing.getId())) {
                receivedNotification = true;
                break;
            }
        }
        Assertions.assertTrue(receivedNotification);
    }


    @Given("People have liked a listing including myself")
    public void iPeopleHaveLikedAListingIncludingMyself() {
        businessRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(user);
        userTwo = userRepository.save(userTwo);

        business = new Business(user.getId(), "Business", "cool business", address,
                "Accommodation", LocalDate.now());
        business = businessRepository.save(business);

        product = productRepository.save(new Product(business.getId(), business, "Apple", "Apple is red",
                "Apple Company", 1.0, date));
        product = productRepository.save(product);

        inventoryItem = new InventoryItem(product, 10, 1.5d, 250d, null, null, null, LocalDate.now());
        inventoryItem = inventoryRepository.save(inventoryItem);

        listing = new Listing(inventoryItem, 10, 10d, null, date, date);
        listing = listingRepository.save(listing);

        likesService.addLikeToListing(listing.getId(), user);
        likesService.addLikeToListing(listing.getId(), userTwo);
        user = userRepository.save(user);
        userTwo = userRepository.save(userTwo);
        Assertions.assertTrue(listing.hasUserLikedListing(user));
    }

    @When("Someone buys that listing")
    public void someoneBuysThatListing() {
        listingService.buyListing(listing.getId(), userTwo);
        Assertions.assertNull(listingRepository.getListingById(listing.getId())); //Listing was bought
    }

    @Then("I receive a notification that the specific listing has sold")
    public void iReceiveANotificationThatTheSpecificListingHasSold() {
        boolean receivedNotification = false;
        List<Notification> notifications = notificationRepository.getNotificationsByRecipient(user);
        for (Notification notification : notifications) {
            if (notification.getMessage().contains("sold")) {
                receivedNotification = true;
                break;
            }
        }
        Assertions.assertTrue(receivedNotification);
    }

    @When("I buy that listing")
    public void iBuyThatListing() {
        listingService.buyListing(listing.getId(), user);
        Assertions.assertNull(listingRepository.getListingById(listing.getId())); //Listing was bought
    }

    @Then("every other user who liked the listing receives a notification, but not myself")
    public void everyOtherUserWhoLikedTheListingReceivesANotificationButNotMyself() {
        //We do not want to see a notification
        boolean receivedNotification = false;
        List<Notification> notifications = notificationRepository.getNotificationsByRecipient(user);
        for (Notification notification : notifications) {
            if (notification.getMessage().contains("sold")) {
                receivedNotification = true;
                break;
            }
        }
        Assertions.assertFalse(receivedNotification);

        //Other likers see notification
        receivedNotification = false;
        notifications = notificationRepository.getNotificationsByRecipient(userTwo);
        for (Notification notification : notifications) {
            if (notification.getMessage().contains("sold")) {
                receivedNotification = true;
                break;
            }
        }
        Assertions.assertTrue(receivedNotification);
    }

    @Then("The user receives a notification telling them they have unliked the listing")
    public void theUserReceivesANotificationUnliking() {
        boolean receivedNotification = false;
        List<Notification> notifications = notificationRepository.getNotificationsByRecipient(user);
        for (Notification notification : notifications) {
            if (notification.getType() == NotificationType.LIKED) {
                receivedNotification = true;
                break;
            }
        }
        Assertions.assertTrue(receivedNotification);
    }
}
