package org.seng302.main.services;


import lombok.extern.log4j.Log4j2;
import org.seng302.main.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

/**
 * This class is used to determine if the information entered into the fields by the user are valid.
 */
@Service
@Log4j2
public class UserValidateService {

    /**
     * Add a private constructor to hide the implicit public one.
     */
    private UserValidateService() {
    }

    /**
     * This method determines if the user being entered into database satisfies all the required fields. Returns
     * true is all fields are satisfied (not empty).
     *
     * @param user User to be added and check validity.
     * @return If all the required fields of user contain
     */
    public static boolean checkRequired(User user) {
        if (user.getFirstName() == null || user.getLastName() == null || user.getEmail() == null ||
                user.getDateOfBirth() == null || user.getHomeAddress() == null || user.getPassword() == null) {
            return false;
        }

        return user.getFirstName().length() != 0 && user.getLastName().length() != 0 && user.getEmail().length() != 0 &&
                user.getHomeAddress().isValidAddress() && user.getPassword().length() != 0;
    }

    /**
     * This determines if the users email matches the general pattern of an email
     *
     * @param email Users email to be checked
     * @return True if the users email matches the address format specified
     */
    public static boolean emailCheck(String email) {
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        if (email != null) {
            return VALID_EMAIL_ADDRESS_REGEX.matcher(email).find();
        }
        return false;
    }

    /**
     * As users are required to be atleast 13 years of age, this functions validates the users given DOB
     *
     * @param dob Users dob to be checked
     * @return True if the users age is > 13
     */
    public static boolean ageCheck(LocalDate dob) {
        LocalDate now = LocalDate.now();
        long daysDiff = ChronoUnit.DAYS.between(dob, now);
        return daysDiff >= 13 * 365;

    }

    /**
     * Check if the user information for a name field is alpha
     *
     * @param name Users name to be check
     * @return True if the users name contains only characters
     */
    public static boolean nameCheck(String name) {
        if (name != null) {
            return name.matches("^[a-zA-Z]+$");
        }
        return false;
    }

    /**
     * Check if the user input is too long
     *
     * @param userInput input user details to be checked
     * @param length    the maximum the user details can be
     * @return True if the input is under the specified length
     */
    public static boolean isLessThanSpecified(String userInput, int length) {
        return userInput.length() <= length;
    }

    public static void validateUser(User newUser) {

        // Email addresses on average are 25 characters long, but a leeway of 40 is given
        int maxEmailLength = 40;
        // Some cultures have a large number of characters
        int maxNameLength = 70;

        if (!checkRequired(newUser)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required details (*) missing.");
        }
        if (!emailCheck(newUser.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format.");
        }
        if (!isLessThanSpecified(newUser.getEmail(), maxEmailLength)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email address length, must be equal or less than " + maxEmailLength + ".");
        }
        if (!ageCheck(newUser.getDateOfBirth())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must be 13 or older to register.");
        }
        if (!nameCheck(newUser.getFirstName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "First name must only contain alphabet");
        }
        if (!isLessThanSpecified(newUser.getFirstName(), maxNameLength)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid first name length, must be equal or less than " + maxNameLength + ".");
        }
        if (newUser.getMiddleName() != null && !nameCheck(newUser.getMiddleName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Middle name must only contain alphabet.");
        }
        if (newUser.getMiddleName() != null && !isLessThanSpecified(newUser.getMiddleName(), maxNameLength)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid middle name length, must be equal or less than " + maxNameLength + ".");
        }
        if (!nameCheck(newUser.getLastName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Last name must contain only alphabet characters");
        }
        if (!isLessThanSpecified(newUser.getLastName(), maxNameLength)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid last name length, must be equal or less than " + maxNameLength + ".");
        }
    }

}
