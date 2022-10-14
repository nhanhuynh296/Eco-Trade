package org.seng302.main.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.seng302.main.models.UserImage;

import java.time.LocalDate;
import java.util.List;

/**
 * User response DTO object
 */
@Getter
@Setter
@NoArgsConstructor
public class UserResponse {

    private long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String nickname;
    private String email;
    private String phoneNumber;
    private String bio;
    private AddressResponse homeAddress;
    private LocalDate created;
    private String role;
    private List<BusinessResponse> businessesAdministered;
    private Long primaryImageId;
    private List<UserImage> images;
    private String countryForCurrency;
    private LocalDate dateOfBirth;

}
