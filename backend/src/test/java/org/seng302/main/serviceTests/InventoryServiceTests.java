package org.seng302.main.serviceTests;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.response.InventoryItemResponse;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.models.*;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.InventoryRepository;
import org.seng302.main.repository.ProductImageRepository;
import org.seng302.main.repository.ProductRepository;
import org.seng302.main.services.BusinessTypeService;
import org.seng302.main.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for the InventoryService class
 */
@SpringBootTest
class InventoryServiceTests {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private BusinessRepository businessRepository;
    private Business business;

    User user;
    InventoryItem item;
    Product product;
    LocalDate validSellBy = LocalDate.of(2022, 11, 11);
    LocalDate validBestBy = LocalDate.of(2022, 10, 11);
    LocalDate validManufactured = LocalDate.of(2021, 5, 10);
    LocalDate validExpiry = LocalDate.of(2022, 12, 11);
    Long validItemId = 1L;
    Integer validProductId = 1;
    Integer validQuantity = 3;
    Double validPricePerItem = 14.0;
    Double validTotalPrice = 40.0;
    JSONObject itemJson;
    String defaultSort = "NAME_DESC";


    /**
     * Creates new inventory item object with valid attributes
     */
    @BeforeEach
    public void init() {

        Address address = new Address("1", "Nowhere", "Varrock", "Tirannwn", "Gilenor", "800");
        business = new Business(1L, "Some Business", "Description", address, BusinessTypeService.ACCOMMODATION.toString(), LocalDate.now());
        business = businessRepository.save(business);

        product = new Product(business.getId(), business, "Some Product", "Description",
                "TestCo.", 12d, LocalDate.now());

        product = productRepository.save(product);
        validProductId = product.getId().intValue();

        item = new InventoryItem(product, validQuantity, validPricePerItem, validTotalPrice,
                validManufactured, validSellBy, validBestBy, validExpiry);

        item = inventoryRepository.save(item);
        validItemId = item.getId();

        user = new User();
        user.setId(1L);
        user.setUserRole("ROLE_DEFAULT_ADMIN");
        business.setPrimaryAdministratorId(1L);

        itemJson = new JSONObject();
        itemJson.put("productId", item.getProduct().getId().intValue());
        itemJson.put("quantity", item.getQuantity());
        itemJson.put("pricePerItem", item.getPricePerItem());
        itemJson.put("totalPrice", item.getTotalPrice());
        itemJson.put("manufactured", item.getManufactured());
        itemJson.put("sellBy", item.getSellBy());
        itemJson.put("bestBefore", item.getBestBefore());
        itemJson.put("expires", item.getExpires());
    }

    /**
     * Test getting one inventory item without images
     */
    @Test
    void TestGetOneInventoryItem() {
        product = new Product(business.getId(), business, "Some Product", "Description",
                "TestCo.", 12d, LocalDate.now());
        product = productRepository.save(product);

        business.addItemToCatalogue(product);
        business = businessRepository.save(business);

        InventoryItem invItem = new InventoryItem(product, 10, 1d, 10d, null, null, null, LocalDate.of(2021, 1, 1));
        inventoryRepository.save(invItem);

        List<InventoryItemResponse> array = inventoryService.getInventoryItems(business.getId(), 1, 7, defaultSort).getPaginationElements();
        Assertions.assertEquals(2, array.size());
        Assertions.assertNotNull(array.get(0));
    }

    /**
     * Test getting one inventory item that has images attached
     */
    @Test
    void TestGetOneInventoryItemWithImages() {
        product = new Product(business.getId(), business, "Some Product", "Description",
                "TestCo.", 12d, LocalDate.now());
        product = productRepository.save(product);

        ProductImage imageOne = new ProductImage("~/fileName.test", "~/thumbnailFile.test");
        imageOne.setProduct(product);
        ProductImage imageTwo = new ProductImage("~/otherFile.should.be.here", "~/otherThumb.also.here");
        imageTwo.setProduct(product);

        productImageRepository.save(imageOne);
        productImageRepository.save(imageTwo);

        InventoryItem invItem = new InventoryItem(product, 10, 1d, 10d, null, null, null, LocalDate.of(2021, 1, 1));
        inventoryRepository.save(invItem);

        PaginationInfo<InventoryItemResponse> array = inventoryService.getInventoryItems(business.getId(), 1, 7, defaultSort);
        Assertions.assertEquals(2, array.getTotalElements());
    }

    /**
     * Test getting multiple inventory items
     */
    @Test
    void TestGetMultipleInventoryItems() {
        product = new Product(business.getId(), business, "Some Product", "Description",
                "TestCo.", 12d, LocalDate.now());
        productRepository.save(product);

        InventoryItem invItem = new InventoryItem(product, 10, 1d, 10d, null, null, null, LocalDate.of(2021, 1, 1));
        inventoryRepository.save(invItem);

        PaginationInfo<InventoryItemResponse> array = inventoryService.getInventoryItems(business.getId(), 1, 7, defaultSort);
        Assertions.assertEquals(2, array.getTotalElements());

        product = new Product(business.getId(), business, "Another Product", "Description",
                "TestCo.", 12d, LocalDate.now());
        productRepository.save(product);

        invItem = new InventoryItem(product, 10, 1d, 10d, null, null, null, LocalDate.now());
        inventoryRepository.save(invItem);

        array = inventoryService.getInventoryItems(business.getId(), 1, 7, defaultSort);
        Assertions.assertEquals(3, array.getTotalElements());
    }

    /**
     * Tests if a valid inventory item will have the expected results
     */
    @Test
    @Transactional
    void testValidInventoryItem() {
        String errorMessage = inventoryService.isValidInventoryItem(item, business.getId());
        assertEquals("", errorMessage);
    }

    /**
     * Tests if an invalid quantity will have the expected results
     */
    @Test
    @Transactional
    void testInvalidQuantity() {
        item.setQuantity(-1);
        String errorMessage = inventoryService.isValidInventoryItem(item, business.getId());
        assertEquals("Inventory items cannot have a negative quantity.", errorMessage);
    }

    /**
     * Tests if having a total price that is less than the price per item will
     * produce the expected result.
     */
    @Test
    @Transactional
    void testInvalidPrices() {
        item.setTotalPrice(20.0);
        item.setPricePerItem(30.0);
        String errorMessage = inventoryService.isValidInventoryItem(item, business.getId());
        assertEquals("The total price of an item cannot be less than the price per item.", errorMessage);
    }

    /**
     * Tests if having a manufacturing date before other dates produces the expected error
     */
    @Test
    @Transactional
    void testInvalidManufacturedDate() {
        LocalDate invalidManufactured = LocalDate.of(2023, 5, 10);
        item.setManufactured(invalidManufactured);
        String errorMessage = inventoryService.isValidInventoryItem(item, business.getId());
        assertEquals("Items cannot be manufactured after their best by, sell by, or expiry dates.\n" +
                "Items cannot have a manufactured date that is in the future.", errorMessage);
    }

    /**
     * Tests if having an expiry date in the past produces the expected error
     */
    @Test
    @Transactional
    void testInvalidExpiryDate() {
        LocalDate invalidExpiry = LocalDate.of(2019, 5, 10);
        item.setExpires(invalidExpiry);
        String errorMessage = inventoryService.isValidInventoryItem(item, business.getId());
        assertEquals("Expiry date cannot be in the past.\n" +
                "Items cannot be manufactured after their best by, sell by, or expiry dates.\n" +
                "Items cannot have a best by date that is after their expiry date.\n" +
                "Items cannot have a sell by date that is after their expiry date.", errorMessage);
    }

    /**
     * Test getting and setting details
     */
    @Test
    void testSetterGetters() {

        // Test getters
        assertEquals(item.getId(), validItemId);
        assertEquals(item.getProduct().getId().intValue(), validProductId);
        assertEquals(item.getQuantity(), validQuantity);
        assertEquals(item.getPricePerItem(), validPricePerItem);
        assertEquals(item.getTotalPrice(), validTotalPrice);
        assertEquals(item.getManufactured(), validManufactured);
        assertEquals(item.getSellBy(), validSellBy);
        assertEquals(item.getBestBefore(), validBestBy);
        assertEquals(item.getExpires(), validExpiry);

        // Test setters
        item.setId(null);
        item.setProduct(null);
        item.setQuantity(1);
        item.setPricePerItem(null);
        item.setTotalPrice(null);
        item.setManufactured(null);
        item.setSellBy(null);
        item.setBestBefore(null);
        item.setExpires(validExpiry);
        assertNull(item.getId());
        assertNull(item.getProduct());
        assertEquals(1, item.getQuantity());
        assertNull(item.getPricePerItem());
        assertNull(item.getTotalPrice());
        assertNull(item.getManufactured());
        assertNull(item.getSellBy());
        assertNull(item.getBestBefore());
        assertEquals(validExpiry, item.getExpires());
    }

    /**
     * Tests that when using an invalid inventory item that the function returns -1
     */
    @Test
    void testInvalidInventoryItemIdUpdateInventoryItem() {
        itemJson.put("productId", "This should be a number");
        int result = inventoryService.updateInventoryItem(item.getId(), business.getId(), itemJson, user);
        assertEquals(-1, result);
    }

    /**
     * Tests that when using an invalid inventory item that the function returns -1
     */
    @Test
    void testInvalidInventoryItemUpdateInventoryItem() {
        itemJson.put("quantity", -1);
        int result = inventoryService.updateInventoryItem(item.getId(), business.getId(), itemJson, user);
        assertEquals(-2, result);
    }

    /**
     * Tests that when using an invalid user that the function returns -2
     */
    @Test
    @Transactional
    void testNotBusinessAdminNotDGAAUpdateInventoryItem() {
        User badUser = new User();
        badUser.setId(2L);
        badUser.setUserRole("ROLE_USER");

        int result = inventoryService.updateInventoryItem(item.getId(), business.getId(), itemJson, badUser);
        assertEquals(-3, result);
    }

    /**
     * Tests that when using a valid inventory item that the function returns 0
     */
    @Test
    @Transactional
    void testAllValidUpdateInventoryItem() {
        int result = inventoryService.updateInventoryItem(item.getId(), business.getId(), itemJson, user);
        assertEquals(0, result);
    }

}
