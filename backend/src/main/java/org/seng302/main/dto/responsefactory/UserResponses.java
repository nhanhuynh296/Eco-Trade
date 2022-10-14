package org.seng302.main.dto.responsefactory;

import org.seng302.main.dto.response.UserResponse;
import org.seng302.main.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * User Response factory builder class
 */
public class UserResponses {

    /**
     * Add explicit private constructor
     */
    private UserResponses() {}

    /**
     * Creates a single response for user
     *
     * @param user instance of User
     * @param getFullAddress boolean for setting full address
     * @param getBusinesses boolean for setting business
     * @return user response
     */
    public static UserResponse getResponse(User user, boolean getFullAddress, boolean getBusinesses) {
        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setMiddleName(user.getMiddleName());
        userResponse.setNickname(user.getNickname());
        userResponse.setBio(user.getBio());
        userResponse.setHomeAddress(AddressResponses.getResponse(user.getHomeAddress(), getFullAddress));
        userResponse.setCreated(user.getCreated());
        userResponse.setRole(user.getUserRole());
        userResponse.setPrimaryImageId(user.getPrimaryImageId());
        userResponse.setCountryForCurrency(user.getCountryForCurrency());
        userResponse.setImages(user.getImages());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setDateOfBirth(user.getDateOfBirth());

        if (getBusinesses) {
            userResponse.setBusinessesAdministered(BusinessResponses.getAllResponses(user.getBusinessesAdministered(), true, false));
        }

        return userResponse;
    }

    /**
     * Creates a list of user responses
     *
     * @param users List of User instances
     * @param getFullAddress boolean for setting full address
     * @param getBusinesses boolean for setting business
     * @return List of user responses
     */
    public static List<UserResponse> getAllResponses(List<User> users, boolean getFullAddress, boolean getBusinesses) {
        List<UserResponse> userResponses = new ArrayList<>();

        for (User user: users) {
            userResponses.add(getResponse(user, getFullAddress, getBusinesses));
        }

        return userResponses;
    }
}
