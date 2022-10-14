package org.seng302.main.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.seng302.main.models.Address;
import org.seng302.main.models.Business;
import org.seng302.main.models.User;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserRequest {
    private long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String nickname;
    private String bio;
    private String email;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private Address homeAddress;
    private LocalDate created;
    private String userRole;
    private List<Business> businessAdministrated;
    private String password;
    private String countryForCurrency;
    private Long primaryImageId;

    public User getUserObject() {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMiddleName(middleName);
        user.setNickname(nickname);
        user.setBio(bio);
        user.setEmail(email);
        user.setDateOfBirth(dateOfBirth);
        user.setPhoneNumber(phoneNumber);
        user.setHomeAddress(homeAddress);
        user.setCreated(created);
        user.setUserRole(userRole);
        user.setBusinessesAdministered(businessAdministrated);
        user.setPassword(password);
        user.setCountryForCurrency(countryForCurrency);
        user.setPrimaryImageId(primaryImageId);
        return user;
    }
}
