package org.seng302.main.services;


import org.seng302.main.models.Address;

/**
 * This class is used to determine if the business information entered into the fields by the user are valid.
 */
public class BusinessValidateService {

    /**
     * Add a private constructor to hide the implicit public one.
     */
    private BusinessValidateService() {}

    /**
     * Checks the name or ais not null or empty
     */
    public static boolean isEmpty(String name) {
        return name == null || name.strip().length() == 0;
    }

    /**
     * Checks the name is not null or empty
     */
    public static boolean nameTooLong(String name, int maxNameLength) {
        return name.length() >= maxNameLength;
    }

    /**
     * Checks if the address country is invalid
     */
    public static boolean isInvalidAddress(Address address) {
        return address == null || !address.isValidAddress();
    }

}
