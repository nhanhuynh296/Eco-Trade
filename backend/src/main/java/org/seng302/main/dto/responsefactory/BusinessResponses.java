package org.seng302.main.dto.responsefactory;

import org.seng302.main.dto.response.BusinessResponse;
import org.seng302.main.models.Business;

import java.util.ArrayList;
import java.util.List;

/**
 * Business Response factory builder class
 */
public class BusinessResponses {

    /**
     * Add explicit private constructor
     */
    private BusinessResponses() {}

    /**
     * Creates a single partial response for business
     *
     * @param business instance of Business
     * @return BusinessResponse entity
     */
    public static BusinessResponse getPartialResponse(Business business) {
        BusinessResponse businessResponse = new BusinessResponse();

        businessResponse.setId(business.getId());
        businessResponse.setName(business.getName());
        businessResponse.setAddress(AddressResponses.getResponse(business.getAddress(), true));
        businessResponse.setBusinessType(business.getBusinessType());
        businessResponse.setCountryForCurrency(business.getCountryForCurrency());

        return businessResponse;
    }

    /**
     * Creates a single full response for business
     *
     * @param business instance of Business
     * @param getUsers boolean for setting users
     * @return BusinessResponse entity
     */
    public static BusinessResponse getFullResponse(Business business, boolean getUsers) {
        BusinessResponse businessResponse = new BusinessResponse();

        businessResponse.setId(business.getId());
        businessResponse.setDescription(business.getDescription());
        businessResponse.setName(business.getName());
        businessResponse.setAddress(AddressResponses.getResponse(business.getAddress(), true));
        businessResponse.setPrimaryAdministratorId(business.getPrimaryAdministratorId());
        businessResponse.setCountryForCurrency(business.getCountryForCurrency());
        businessResponse.setBusinessType(business.getBusinessType());
        businessResponse.setCreated(business.getCreated());

        if (getUsers) {
            businessResponse.setAdministrators(UserResponses.getAllResponses(business.getAdministrators(), false, false));
        }

        return businessResponse;
    }

    /**
     * Creates a list of business responses
     *
     * @param businesses list of Business entities
     * @param full boolean for getting either full or partial business response
     * @param getUsers boolean for setting users
     * @return List of business responses
     */
    public static List<BusinessResponse> getAllResponses(List<Business> businesses, boolean full, boolean getUsers) {
        List<BusinessResponse> businessResponses = new ArrayList<>();

        for (Business business: businesses) {
            if (full) {
                businessResponses.add(getFullResponse(business, getUsers));
            } else {
                businessResponses.add(getPartialResponse(business));
            }
        }

        return businessResponses;
    }

}
