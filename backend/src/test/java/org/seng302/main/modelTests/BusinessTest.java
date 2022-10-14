package org.seng302.main.modelTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.models.Address;
import org.seng302.main.models.Business;
import org.seng302.main.models.Product;
import org.seng302.main.models.User;
import org.seng302.main.services.BusinessTypeService;
import org.seng302.main.services.BusinessValidateService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.seng302.main.services.BusinessTypeService.isValidType;


/**
 * Test business and address objects.
 */
class BusinessTest {

    private Business testBusiness;
    private Address testAddress;
    private Product testProduct;
    private Address invalidAddressOne;
    private Address invalidAddressTwo;

    /**
     * Creates new business object with valid address object
     */
    @BeforeEach
    public void init() {

        testBusiness = new Business();

        testAddress = new Address("3/24", "Ilam Road", "Christchurch", "Canterbury", "New Zealand"
                , "90210");
        // Invalid address entity
        invalidAddressOne = new Address("", "", "", "", "    ", "");
        invalidAddressTwo = new Address("", "", "", "", null, "");

        testProduct = new Product(testBusiness.getId(), testBusiness, "Watties", "Beans in a can", "Heinz Product Limited", 2.2, LocalDate.now());

        testBusiness.setName("Lumbridge General Store");
        testBusiness.setDescription("A one-stop shop for all your adventuring needs");
        testBusiness.setAddress(testAddress);
        testBusiness.setBusinessType("Accommodation");
        testBusiness.setCreated(LocalDate.now());
    }

    /**
     * Test toString method of address entity.
     */
    @Test
    void testAddressToString() {
        assertEquals("3/24, Ilam Road, Christchurch, Canterbury, New Zealand, 90210", testBusiness.getAddress().toString());
    }

    /**
     * Ensures business type of business is valid
     */
    @Test
    void checkInvalidBusinessType() {
        assertTrue(isValidType(testBusiness.getBusinessType()));

        testBusiness.setBusinessType("Error");

        assertFalse(isValidType(testBusiness.getBusinessType()));
    }

    /**
     * Ensures all types of business types is current enum are valid
     */
    @Test
    void checkAllBusinessType() {
        for (BusinessTypeService type : BusinessTypeService.values()) {
            testBusiness.setBusinessType(type.toString());
            assertTrue(isValidType(testBusiness.getBusinessType()));
        }
    }

    /**
     * Ensures address of business is valid
     */
    @Test
    void testBusinessAddress() {
        assertTrue(testBusiness.getAddress().isValidAddress());

        //Set address to empty
        testBusiness.setAddress(new Address());

        assertFalse(testBusiness.getAddress().isValidAddress());

    }

    /**
     * Add admin to administrators
     */
    @Test
    void testAddAdminToBusiness() {
        User testUser = new User();

        testBusiness.addAdmin(testUser);

        assertTrue(testBusiness.getAdministrators().contains(testUser));

    }

    /**
     * Remove admin from list of administrators
     */
    @Test
    void testRemoveAdminFromBusiness() {
        User testUser = new User();

        testBusiness.addAdmin(testUser);

        assertTrue(testBusiness.getAdministrators().contains(testUser));

        testBusiness.removeAdmin(testUser);

        assertFalse(testBusiness.getAdministrators().contains(testUser));
    }

    /**
     * Ensure correct size of administrator list (multiple admins)
     */
    @Test
    void testSizeOfAdministratorsList() {
        User testUser1 = new User();
        testBusiness.addAdmin(testUser1);
        User testUser2 = new User();
        testBusiness.addAdmin(testUser2);
        User testUser3 = new User();
        testBusiness.addAdmin(testUser3);

        assertEquals(3, testBusiness.getAdministrators().size());

        testBusiness.removeAdmin(testUser3);

        assertEquals(2, testBusiness.getAdministrators().size());
    }

    /**
     * Ensure you can add a product to a businesses catalogue
     */
    @Test
    void addProductToBusinessCatalogue() {

        testBusiness.addItemToCatalogue(testProduct);

        assertTrue(testBusiness.getItemsBusinessCatalogue().contains(testProduct));
    }

    /**
     * Ensure you can remove an items from a businesses catalogue
     */
    @Test
    void removeProductFromBusinessCtalogue() {
        testBusiness.addItemToCatalogue(testProduct);
        assertTrue(testBusiness.getItemsBusinessCatalogue().contains(testProduct));

        testBusiness.removeItemFromCatalogue(testProduct);
        assertFalse(testBusiness.getItemsBusinessCatalogue().contains(testProduct));
    }

    /**
     * Ensures item added to catalogue is correct item
     */
    @Test
    void ensureCorrectItemAddedToCatalogue() {
        testBusiness.addItemToCatalogue(testProduct);

        assertTrue(testBusiness.getItemsBusinessCatalogue().contains(testProduct));
    }

    /**
     * Ensure correct size of item catalogue list
     */
    @Test
    void testSizeOfBusinessCatalogue() {
        Product prod1 = new Product();
        testBusiness.addItemToCatalogue(prod1);
        Product prod2 = new Product();
        testBusiness.addItemToCatalogue(prod2);
        Product prod3 = new Product();
        testBusiness.addItemToCatalogue(prod3);
        Product prod4 = new Product();
        testBusiness.addItemToCatalogue(prod4);

        assertEquals(4, testBusiness.getItemsBusinessCatalogue().size());

        testBusiness.removeItemFromCatalogue(prod1);

        assertEquals(3, testBusiness.getItemsBusinessCatalogue().size());
        assertFalse(testBusiness.getItemsBusinessCatalogue().contains(prod1));
    }


    /**
     * Test a valid business name returns false
     */
    @Test
    void checkValidBusinessName() {
        assertFalse(BusinessValidateService.isEmpty(testBusiness.getName()));
    }


    /**
     * Test an empty string returns true
     */
    @Test
    void checkInvalidStringBusinessName() {
        assertTrue(BusinessValidateService.isEmpty("        "));
    }


    /**
     * Test a null returns true
     */
    @Test
    void checkInvalidNullBusinessName() {
        assertTrue(BusinessValidateService.isEmpty(null));
    }


    /**
     * Test a valid name length returns false
     */
    @Test
    void validBusinessNameLength() {
        assertFalse(BusinessValidateService.nameTooLong(testBusiness.getName(), 70));
    }


    /**
     * Test an invalid name length returns true
     */
    @Test
    void invalidBusinessNameLength() {
        assertTrue(BusinessValidateService.nameTooLong(testBusiness.getName(), 1));
    }


    /**
     * Tests a valid country address returns false
     */
    @Test
    void validAddress() {
        assertFalse(BusinessValidateService.isInvalidAddress(testAddress));
    }


    /**
     * Tests the country address cannot be empty
     */
    @Test
    void invalidEmptyAddress() {
        assertTrue(BusinessValidateService.isInvalidAddress(invalidAddressOne));
    }


    /**
     * Tests the country address cannot be null
     */
    @Test
    void invalidNullAddress() {
        assertTrue(BusinessValidateService.isInvalidAddress(invalidAddressTwo));
    }

}
