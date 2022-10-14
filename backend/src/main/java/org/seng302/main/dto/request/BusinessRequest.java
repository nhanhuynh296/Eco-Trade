package org.seng302.main.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.seng302.main.models.Business;

@Getter
@Setter
@NoArgsConstructor
public class BusinessRequest {
    private String name;
    private String description;
    private AddressRequest address;
    private String businessType;


    /**
     * Create business with missing attributes
     * @return {@link Business} without primaryAdministratorId
     */
    public Business getBusinessObject() {
        Business business = new Business();
        business.setName(name);
        business.setDescription(description);
        business.setAddress(address.getAddressObject());
        business.setBusinessType(businessType);
        return business;
    }

}
