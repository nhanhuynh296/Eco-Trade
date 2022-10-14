package org.seng302.main.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Address Data Transfer Object
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressResponse {

    private String streetNumber;
    private String streetName;
    private String city;
    private String region;
    private String country;
    private String postcode;

}
