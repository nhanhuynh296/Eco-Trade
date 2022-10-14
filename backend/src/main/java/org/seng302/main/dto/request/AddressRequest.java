package org.seng302.main.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.seng302.main.models.Address;

@Getter
@Setter
@NoArgsConstructor
public class AddressRequest {
    private String streetNumber;
    private String streetName;
    private String city;
    private String region;
    private String country;
    private String postcode;

    /**
     * Return a fully working version of object address
     */
    public Address getAddressObject() {
        return new Address(streetNumber, streetName, city, region, country, postcode);
    }
}
