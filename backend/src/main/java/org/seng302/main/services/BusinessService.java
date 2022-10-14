package org.seng302.main.services;

import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.seng302.main.dto.request.BusinessRequest;
import org.seng302.main.dto.response.BusinessResponse;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.dto.responsefactory.ResponseFactory;
import org.seng302.main.models.Business;
import org.seng302.main.models.User;
import org.seng302.main.repository.AddressRepository;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

/**
 * Business Service that communicates with Business Repo
 */
@Service
@Log4j2
public class BusinessService {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    protected AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    private ResponseFactory responseFactory = new ResponseFactory();


    /**
     * Creates a new business
     *
     * @param newBusiness new business data
     * @return JSONObject of the new created business id
     */
    public JSONObject createBusiness(BusinessRequest newBusiness, User user) {
        if (newBusiness.getName() == null || newBusiness.getName().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Business name must not be empty");
        }
        if (newBusiness.getAddress() == null || !newBusiness.getAddress().getAddressObject().isValidAddress()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Business address country must not be empty");
        } else if (newBusiness.getBusinessType() == null || !BusinessTypeService.isValidType(newBusiness.getBusinessType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Business type is invalid or empty");
        }

        Business business = newBusiness.getBusinessObject();
        business.setPrimaryAdministratorId(user.getId());
        business.addAdmin(user);
        business.setCreated(LocalDate.now());

        business.setAddress(addressRepository.save(business.getAddress()));
        business = businessRepository.save(business);

        JSONObject entity = new JSONObject();
        entity.put("businessId", business.getId());

        return entity;
    }

    /**
     * Find business by business ID
     *
     * @param businessId ID of business
     * @return Business object or null
     */
    public Business getBusinessById(long businessId) {
        return businessRepository.findBusinessById(businessId);
    }

    /**
     * Gets the countryForCurrency of a business based on the businessId from the database
     * -- countryForCurrency is a variable that contains the country the user wants to use
     * -- for the currency display of their business
     *
     * @param businessId The id of the business we're querying
     * @return A JSONObject that contains the country
     */
    public String getBusinessCountry(long businessId) {
        Business business = businessRepository.findBusinessById(businessId);
        return business.getCountryForCurrency();
    }

    /**
     * Retrieves a business from the database using a business id
     *
     * @param id of the business
     * @return JSONObject with the business information
     */
    public BusinessResponse getBusiness(Long id) {
        Business business = businessRepository.findBusinessById(id);

        if (business == null) {
            return null;
        }

        return responseFactory.getBusinessResponse(business);
    }

    /**
     * Makes the requested user an admin of the business
     *
     * @param requestedUserId the id of the requested user
     * @param id of the business
     * @param user instance of the current user
     * @return int -1 if the requested user does not exist
     * int -2 if the current user is not the primary admin of the business
     * int -3 if the requested user is already an admin
     * int 0 if successful
     */
    public int makeUserAdminBusiness(long requestedUserId, long id, User user) {
        User requestedUser = userRepository.findUserById(requestedUserId);
        Business business = businessRepository.findBusinessById(id);

        if (requestedUser == null) {
            return -1;
        }
        if (!isMainBusinessAdminOrGAA(user, business)) {
            return -2;
        }
        if (business.getAdministrators().contains(requestedUser) || requestedUserId == business.getPrimaryAdministratorId()) {
            return -3;
        }

        business.addAdmin(requestedUser);
        businessRepository.save(business);
        return 0;
    }

    /**
     * Removes the requested user from being an admin of the business
     *
     * @param requestedUserId the id of the requested user
     * @param id of the business
     * @param user instance of the current user
     * @return int -1 if the requested user does not exist
     * int -2 if the current user is not the primary admin of the business
     * int -3 if the business does not contain the requested user as an admin OR the requested user is the primary admin of the business
     * int 0 if successful
     */
    public int removeUserAdminBusiness(long requestedUserId, long id, User user) {
        User requestedUser = userRepository.findUserById(requestedUserId);
        Business business = businessRepository.findBusinessById(id);

        if (requestedUser == null) {
            return -1;
        }
        if (!isMainBusinessAdminOrGAA(user, business)) {
            return -2;
        }
        if (!business.getAdministrators().contains(requestedUser) || requestedUserId == business.getPrimaryAdministratorId()) {
            return -3;
        }

        business.removeAdmin(requestedUser);
        businessRepository.save(business);
        return 0;
    }

    /**
     * Retrieves the search query result in a paginated form with total pages and elements values
     *
     * @param specs the search query
     * @param pageNumber int value of the page
     * @param pageSize int value of the page size
     * @param sortBy String containing sort by option
     * @return List<?> of all the businesses in the page and number of total pages and elements
     */
    @Transactional
    public PaginationInfo<BusinessResponse> businessSearch(Specification<Business> specs, int pageNumber, int pageSize, String sortBy) {
        Sort sort;
        Pageable pageRequest;

        switch (sortBy) {
            case "DATE_ASC":
                sort = Sort.by(Sort.Order.asc("created").ignoreCase());
                break;
            case "DATE_DESC":
                sort = Sort.by(Sort.Order.desc("created").ignoreCase());
                break;
            case "TYPE_ASC":
                sort = Sort.by(Sort.Order.asc("businessType").ignoreCase());
                break;
            case "TYPE_DESC":
                sort = Sort.by(Sort.Order.desc("businessType").ignoreCase());
                break;
            case "NAME_ASC":
                sort = Sort.by(Sort.Order.asc("name").ignoreCase());
                break;
            case "NAME_DESC":
                sort = Sort.by(Sort.Order.desc("name").ignoreCase());
                break;
            default:
                sort = null;
                break;
        }

        if (sort != null) {
            pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        } else {
            pageRequest = PageRequest.of(pageNumber, pageSize);
        }

        Page<Business> page = businessRepository.findAll(Specification.where(specs), pageRequest);
        List<Business> businesses = page.getContent();

        int totalPages = page.getTotalPages();
        long totalElements = page.getTotalElements();

        return new PaginationInfo<>(responseFactory.getBusinessResponses(businesses), totalPages, totalElements);
    }

    /**
     * Deletes the business by using the provided business id
     *
     * @param id of the business
     */
    public void deleteBusiness(Long id) {
        businessRepository.deleteById(id);
    }

    /**
     * Checks if the user is the primary admin of the business OR a DGAA
     *
     * @param user the current user
     * @param business instance of the business
     * @return boolean if the user is a main admin
     */
    public boolean isMainBusinessAdminOrGAA(User user, Business business) {
        return user != null && (user.getId().equals(business.getPrimaryAdministratorId()) || userService.isApplicationAdmin(user));
    }

    /**
     * Checks if the user is an admin of the business OR is a GAA
     *
     * @param user instance of the user
     * @param businessId ID of the business
     * @return boolean if the user is an admin of the business OR is a GAA
     */
    public boolean isAdminOfBusinessOrGAAFromBusinessId(User user, long businessId) {
        Business business = businessRepository.findBusinessById(businessId);
        return business != null && isAdminOfBusinessOrGAA(user, business);
    }

    /**
     * Checks if the user is an admin of the business OR is a GAA
     *
     * @param user instance of the user
     * @param business instance of the business
     * @return boolean if the user is an admin of the business OR is a GAA
     */
    public boolean isAdminOfBusinessOrGAA(User user, Business business) {
        return user != null && (business.getAdministrators().contains(user) || isMainBusinessAdminOrGAA(user, business));
    }

}
