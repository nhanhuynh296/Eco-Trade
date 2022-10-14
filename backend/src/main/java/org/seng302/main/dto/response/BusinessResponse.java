package org.seng302.main.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Business Data Transfer Object
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessResponse {

    private Long id;
    private List<UserResponse> administrators;
    private String description;
    private String name;
    private AddressResponse address;
    private Long primaryAdministratorId;
    private String countryForCurrency;
    private String businessType;
    private LocalDate created;

}
