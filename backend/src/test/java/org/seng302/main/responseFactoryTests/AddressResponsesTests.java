package org.seng302.main.responseFactoryTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.response.AddressResponse;
import org.seng302.main.dto.responsefactory.AddressResponses;
import org.seng302.main.models.Address;

/**
 * Test address responses
 */
class AddressResponsesTests {

    Address testAddress;

    AddressResponse testAddressResponsePartial;
    AddressResponse testAddressResponseFull;

    /**
     * Initialising all the variables before each test
     */
    @BeforeEach
    public void init() {
        testAddress = new Address("3/24", "Ilam Road", "Christchurch",
                "Canterbury", "New Zealand", "90210");

        testAddressResponsePartial = new AddressResponse();
        testAddressResponsePartial.setCountry("New Zealand");
        testAddressResponsePartial.setRegion("Canterbury");
        testAddressResponsePartial.setCity("Christchurch");

        testAddressResponseFull = new AddressResponse();
        testAddressResponseFull.setCountry("New Zealand");
        testAddressResponseFull.setRegion("Canterbury");
        testAddressResponseFull.setCity("Christchurch");
        testAddressResponseFull.setStreetName("Ilam Road");
        testAddressResponseFull.setStreetNumber("3/24");
        testAddressResponseFull.setPostcode("90210");
    }

    /**
     * Testing for getting partial address response
     */
    @Test
    void testGettingPartialAddress() {
        AddressResponse response = AddressResponses.getResponse(testAddress, false);

        Assertions.assertEquals(testAddressResponsePartial.getCountry(), response.getCountry());
        Assertions.assertEquals(testAddressResponsePartial.getRegion(), response.getRegion());
        Assertions.assertEquals(testAddressResponsePartial.getCity(), response.getCity());
        Assertions.assertNull(response.getStreetName());
        Assertions.assertNull(response.getStreetNumber());
        Assertions.assertNull(response.getPostcode());
    }

    /**
     * Testing for getting full address response
     */
    @Test
    void testGettingFullAddress() {
        AddressResponse response = AddressResponses.getResponse(testAddress, true);

        Assertions.assertEquals(testAddressResponseFull.getCountry(), response.getCountry());
        Assertions.assertEquals(testAddressResponseFull.getRegion(), response.getRegion());
        Assertions.assertEquals(testAddressResponseFull.getCity(), response.getCity());
        Assertions.assertEquals(testAddressResponseFull.getStreetName(), response.getStreetName());
        Assertions.assertEquals(testAddressResponseFull.getStreetNumber(), response.getStreetNumber());
        Assertions.assertEquals(testAddressResponseFull.getPostcode(), response.getPostcode());
    }

}
