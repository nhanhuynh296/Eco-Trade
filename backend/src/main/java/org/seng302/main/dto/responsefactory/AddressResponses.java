package org.seng302.main.dto.responsefactory;

import org.seng302.main.dto.response.AddressResponse;
import org.seng302.main.models.Address;

/**
 * Address Response factory builder class
 */
public class AddressResponses {

    /**
     * Add explicit private constructor
     */
    private AddressResponses() {}

    /**
     * Creates a single response for address
     *
     * @param address instance of Address
     * @param full boolean for getting either full or partial address
     * @return AddressResponse entity
     */
    public static AddressResponse getResponse(Address address, boolean full) {
        AddressResponse addressResponse = new AddressResponse();

        addressResponse.setCountry(address.getCountry());
        addressResponse.setRegion(address.getRegion());
        addressResponse.setCity(address.getCity());

        if (full) {
            addressResponse.setStreetName(address.getStreetName());
            addressResponse.setStreetNumber(address.getStreetNumber());
            addressResponse.setPostcode(address.getPostcode());
        }

        return addressResponse;
    }

}
