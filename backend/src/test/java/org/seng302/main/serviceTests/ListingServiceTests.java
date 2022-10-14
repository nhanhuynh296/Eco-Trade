package org.seng302.main.serviceTests;

import com.sipios.springsearch.SearchCriteria;
import com.sipios.springsearch.SpecificationImpl;
import com.sipios.springsearch.anotation.SearchSpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.response.ListingResponse;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.helpers.NotificationType;
import org.seng302.main.models.*;
import org.seng302.main.repository.*;
import org.seng302.main.services.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SpringBootTest
class ListingServiceTests {

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
    private ListingService listingService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SaleRepository saleRepository;

    private Listing testListing;

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

        testListing = new Listing(testInventory, 2, 4.0, "Beans",
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(5));
        testListing = listingRepository.save(testListing);
    }

    /**
     * Test that an empty quantity returns the correct error message
     */
    @Test
    void testInvalidEmptyQuantity() {
        String message = null;

        // Make the quantity invalid
        testListing.setQuantity(null);
        try {
            message = listingService.getListingErrorCode(testListing);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals("Listing must have a specified quantity.", message);
    }

    /**
     * Test that a quantity larger than what is already in the inventory returns the correct error message
     */
    @Test
    void testInvalidListingQuantityLargerThanInventory() {
        String message = null;

        // Make the quantity invalid
        testListing.setQuantity(testListing.getInventoryItem().getQuantity() + 1);
        try {
            message = listingService.getListingErrorCode(testListing);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals(
                "Listing cannot have quantity larger than the corresponding inventory.", message);
    }

    /**
     * Test that an empty price returns the correct error message
     */
    @Test
    void testInvalidEmptyPrice() {
        String message = null;

        // Make the quantity invalid
        testListing.setPrice(null);
        try {
            message = listingService.getListingErrorCode(testListing);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals("Listing must have a specified price.", message);
    }

    /**
     * Test that if a listing closing date is before the listing created date the correct error message is returned
     */
    @Test
    void testInvalidClosesDayBeforeCreatedDay() {
        String message = null;
        LocalDate invalidDate = testListing.getCreated().minusDays(1);
        // Make the quantity invalid
        testListing.setCloses(invalidDate);
        try {
            message = listingService.getListingErrorCode(testListing);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals("Closing date cannot be before the created date.", message);
    }

    /**
     * Test that if the listing closing date is after the item expiring date, the correct error message is returned
     */
    @Test
    void testInvalidClosesDayAfterExpiry() {
        String message = null;
        LocalDate invalidDate = testListing.getInventoryItem().getExpires().plusDays(2);

        // Make the quantity invalid
        testListing.setCloses(invalidDate);
        try {
            message = listingService.getListingErrorCode(testListing);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals("Closing date cannot be after the expiry date.", message);
    }

    /**
     * Tests that if the listing closing date is in the past, the correct error message is returned
     */
    @Test
    void testInvalidClosesDayInThePast() {
        String message = null;

        // Make the quantity invalid
        testListing.setCloses(LocalDate.now().minusDays(1));
        try {
            message = listingService.getListingErrorCode(testListing);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals("Closing date must be in the future.", message);
    }

    /**
     * Test searching for a listings for a given query
     */
    @Transactional
    @Test
    void testSearchingListings() {
        SearchCriteria searchCriteria = new SearchCriteria("moreInfo", ":", "", "Beans", "");

        SearchSpec annotation = new SearchSpec() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return SearchSpec.class;
            }

            @Override
            public boolean caseSensitiveFlag() {
                return false;
            }

            @Override
            public String searchParam() {
                return null;
            }
        };

        Specification<Listing> specs = new SpecificationImpl<>(searchCriteria, annotation);
        PaginationInfo<ListingResponse> results = listingService.listingSearch(specs, "DATE_ASC", 0, 10);

        Assertions.assertNotEquals(0, results.getTotalElements());
        Assertions.assertEquals("Watties Baked Beans", results.getPaginationElements().get(0).getInventoryItem().getProduct().getName());
    }

    /**
     * Test getting one listing with a given id
     */
    @Test
    @Transactional
    void testGetOneListing() {
        ListingResponse listingResponse = listingService.getOneListing(testListing.getId());
        Assertions.assertEquals(testListing.getId(), listingResponse.getId());
        Assertions.assertEquals(testListing.getInventoryItem().getId(), listingResponse.getInventoryItem().getId());
        Assertions.assertEquals(testListing.getInventoryItem().getProduct().getId(), listingResponse.getInventoryItem().getProduct().getId());
    }

    /**
     * Test that buyListing creates a sale in the repository and deletes the original listing
     */
    @Test
    @Transactional
    void testBuyListing() {
        Sale sale = listingService.buyListing(testListing.getId(), null);
        Listing checkListing = listingRepository.getListingById(testListing.getId());
        Assertions.assertNull(checkListing);
        Optional<Sale> checkSale = saleRepository.findById(sale.getSaleId());
        Assertions.assertTrue(checkSale.isPresent());
        Sale retrievedSale = checkSale.get();
        Assertions.assertEquals(sale.toString(), retrievedSale.toString());
    }


    /**
     * Check notification is generated when buying a listing
     */
    @Transactional
    @Test
    void testBoughtNotification() {
        Sale sale = listingService.buyListing(testListing.getId(), user);
        boolean receivedNotification = false;
        List<Notification> notifications = notificationRepository.getNotificationsByRecipient(user);

        for (Notification notification : notifications){
            if (Objects.equals(notification.getSale().getSaleId(), sale.getSaleId()) && notification.getType() == NotificationType.BOUGHT){
                receivedNotification = true;
                break;
            }
        }
        Assertions.assertTrue(receivedNotification);
    }
}
