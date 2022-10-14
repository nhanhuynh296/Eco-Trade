package gradle.cucumber.listings;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.seng302.main.helpers.NotificationType;
import org.seng302.main.models.*;
import org.seng302.main.repository.*;
import org.seng302.main.services.BusinessTypeService;
import org.seng302.main.services.ListingService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Transactional
public class BuyListingStepDefinitions {


    @Autowired
    UserRepository userRepository;

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    ListingRepository listingRepository;

    @Autowired
    SaleRepository saleRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    ListingService listingService;

    Address address = new Address("3/24", "Ilam Road", "Christchurch",
            "Canterbury", "New Zealand", "90210");
    User user = new User("Juice", "", "Boy", "JB", "", "jb@juice.com",
            LocalDate.now(), "", address, LocalDate.now(), "ROLE_USER", "password123", "123", null);
    Business business = new Business(user.getId(), "Juice Store", "Description1", new Address(), BusinessTypeService.ACCOMMODATION.toString(), LocalDate.now());
    Product product;
    InventoryItem inventoryItem;
    Listing listing;
    Sale sale;
    Notification notif;

    @Given("I am logged-in")
    public void iAmLoggedIn() {
        user = userRepository.save(user);
        Assertions.assertNotNull(user);
    }

    @When("I buy a listing")
    public void i_buy_a_listing() {
        saveListing();
        sale = listingService.buyListing(listing.getId(), user);
    }

    @Then("The listing is removed from future searches")
    public void the_listing_is_removed_from_future_searches() {
        Listing checkListing = listingRepository.getListingById(listing.getId());
        Assertions.assertNull(checkListing);
    }

    @Then("Information about the sale is recorded in a sales history for the seller’s business.")
    public void information_about_the_sale_is_recorded_in_a_sales_history_for_the_seller_s_business() {
        Optional<Sale> checkSale = saleRepository.findById(sale.getSaleId());
        Assertions.assertTrue(checkSale.isPresent());
        Sale retrievedSale = checkSale.get();
        Assertions.assertEquals(sale.getListingDate(), retrievedSale.getListingDate());
        Assertions.assertEquals(sale.getProduct().getId(), retrievedSale.getProduct().getId());
        Assertions.assertEquals(sale.getQuantity(), retrievedSale.getQuantity());
        Assertions.assertEquals(sale.getSaleDate(), LocalDate.now());
        Assertions.assertEquals(sale.getSoldFor(), listing.getPrice());
        Assertions.assertEquals(sale.getBusinessId(), listing.getInventoryItem().getProduct().getBusinessId());
    }

    @Then("I receive a notification about the purchase confirmation")
    public void i_receive_a_notification_about_the_purchase_confirmation() {
        boolean receivedNotification = false;
        List<Notification> notifications = notificationRepository.getNotificationsByRecipient(user);

        for (Notification notification : notifications){
            if (Objects.equals(notification.getSale().getSaleId(), sale.getSaleId()) && notification.getType() == NotificationType.BOUGHT){
                notif = notification;
                receivedNotification = true;
                break;
            }
        }
        Assertions.assertTrue(receivedNotification);
    }

    @And("The notification contains what I have purchased, price and pickup location")
    @Transactional
    public void the_notification_contains_what_i_have_purchased(){
        Assertions.assertEquals(notif.getSale().getSaleId(), sale.getSaleId());
        Assertions.assertEquals(notif.getSale().getSoldFor(), sale.getSoldFor());
    }

    private void saveListing() {
        business = businessRepository.save(business);
        product = new Product(business.getId(), business, "Product name", "Description", "Prefab Co.", 1.5d, LocalDate.now());
        product = productRepository.save(product);
        inventoryItem = new InventoryItem(product, 10, 1.5d, 250d, null, null, null, LocalDate.now());
        inventoryItem = inventoryRepository.save(inventoryItem);

        listing = new Listing(inventoryItem, 100, 1.00, "Listing info", LocalDate.now(), LocalDate.now().minusWeeks(2));
        listing = listingRepository.save(listing);
    }


}
