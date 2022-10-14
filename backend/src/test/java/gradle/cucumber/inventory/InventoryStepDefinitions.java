package gradle.cucumber.inventory;

import gradle.cucumber.SpringIntegrationTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.seng302.main.models.*;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.InventoryRepository;
import org.seng302.main.repository.ProductRepository;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.BusinessTypeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

public class InventoryStepDefinitions extends SpringIntegrationTest {
    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    // Item ID of the product with a set name
    Long itemId;

    User user = new User("Juice", "", "Boy", "JB", "", "jb@juice.com", LocalDate.now(), "", new Address("", "", "", "", "", ""), LocalDate.now(), "ROLE_USER", "password123", "123", null);
    Business testBusiness;
    InventoryItem invItem;

    List<InventoryItem> inventoryItemList;

    @Given("I am logged in as an admin for an existing business which contains an item with the name {string} in its inventory")
    public void iAmLoggedInAsAnAdminForAnExistingBusinessWhichContainsAnItemCalledInItsInventory(String itemName) {
        userRepository.deleteAll();
        user = userRepository.save(user);
        testBusiness = new Business(user.getId(), "Juice Store", "Description1", new Address(), BusinessTypeService.ACCOMMODATION.toString(), LocalDate.now());
        testBusiness = businessRepository.save(testBusiness);

        Product product = new Product(testBusiness.getId(), testBusiness, itemName, "Description", "Prefab Co.", 1.5d, LocalDate.now());
        product = productRepository.save(product);

        invItem = new InventoryItem(product, 10, 1.5d, 250d, null, null, null, LocalDate.now());

        invItem = inventoryRepository.save(invItem);
        itemId = invItem.getId();

    }

    @When("I view my businesses inventory")
    public void iViewMyBusinessesInventory() {
        inventoryItemList = inventoryRepository.getAllFromBusinessInventory(testBusiness.getId());
    }

    @Then("I am able to see all details about the item")
    public void iAmAbleToSeeTheInMyInventory() {
        boolean isSeen = false;

        // Essentially look over all the items we got from checking our inventory
        for (InventoryItem item : inventoryItemList) {
            if (item.getId().equals(itemId)) { // If theres a matching ID to our specified item, we can see it!
                isSeen = true;
                break;
            }
        }

        // Have we found the item in our inventory?
        Assertions.assertTrue(isSeen);
    }

    @When("I can update my inventory item, by changing the quantity to {int}")
    public void iEditInventoryItem(Integer newQuantity) {
        inventoryRepository.updateInventoryItem(invItem.getId(), newQuantity, invItem.getPricePerItem(),
                invItem.getTotalPrice(), invItem.getManufactured(), invItem.getSellBy(), invItem.getBestBefore(), invItem.getExpires());
    }

    @Then("I am able to view my changes")
    public void iCheckThatInventoryItemUpdated() {
        InventoryItem updatedItem = inventoryRepository.findById(invItem.getId()).get();
        Assertions.assertEquals(20, updatedItem.getQuantity());
    }
}
