package org.seng302.main.controllers;

import com.sipios.springsearch.anotation.SearchSpec;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.seng302.main.dto.request.BusinessRequest;
import org.seng302.main.dto.response.BusinessResponse;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.helpers.ControllerHelper;
import org.seng302.main.models.Business;
import org.seng302.main.models.User;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Holds all business related API endpoints. Its corresponding service
 * holds all the service modules e.g. parameter validations for post
 * requests.
 */
@Controller
@Log4j2
public class BusinessController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessService businessService;

    @Autowired
    ControllerHelper controllerHelper;

    /**
     * Creates a new business and inserts it into the database
     *
     * @param newBusiness Map of JSON input data to a new Business object
     * @param sessionId String the session of the logged in user
     * @return A ResponseEntity with the appropriate HTTP states and body (JSON of business_id)
     */
    @PostMapping("/businesses")
    public ResponseEntity<JSONObject> createBusiness(@RequestBody BusinessRequest newBusiness,
                                                     @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info("[POST /business] Trying to register a new business");

        // If no session id is provided, then the current user is not logged in. (Important for controller tests)
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request.");
        }

        User user = userRepository.findUserBySessionTicket(sessionId);

        JSONObject businessId = businessService.createBusiness(newBusiness, user);
        return new ResponseEntity<>(businessId, HttpStatus.CREATED);
    }

    /**
     * Gets a single business by path variable: id
     *
     * @param id id of business
     * @return JSON of business object
     */
    @GetMapping("/businesses/{id}")
    public ResponseEntity<BusinessResponse> getBusiness(@PathVariable Long id, @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info("[GET /businesses/{id}] Trying to get a business from ID");

        // If no session id is provided, then the current user is not logged in. (Important for controller tests)
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user must be logged in to make this request.");
        }

        BusinessResponse business = businessService.getBusiness(id);

        if (business == null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Business does not exist");
        }

        return ResponseEntity.status(HttpStatus.OK).body(business);
    }

    /**
     * Gets the for country for currency of a business, where the "country for currency" is simply
     * the country for which the business wants to display their product currency in - by default their address
     *
     * @param id The id of the business in question
     * @return 200 OK and a JSONObject containing the country for currency of the queried business
     * 406 not acceptable if the business being queried does not exist
     */
    @GetMapping("/businesses/currency/{id}")
    public ResponseEntity<String> getCountryOfBusinessForCurrency(@PathVariable Long id) {
        log.info("Get /businesses/currency/{id} Trying to get the country of a business for currency");

        try {
            String country = businessService.getBusinessCountry(id);
            return ResponseEntity.status(HttpStatus.OK).body(country);
        } catch (Exception err) {
            log.info("Error trying to get the country of a business for currency", err);
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Business with this id does not exist");
        }
    }

    /**
     * Adds an individual as an administrator for a business.
     * Will not work for businesses or non Global or Primary Administrator types of users.
     *
     * @param user JSON object containing the id of the selected user
     * @param id id of the business from the path
     * @param sessionId the session id of the logged in user
     * @return 200 ok if the user was added successfully as an administrator of the business
     * 400 bad request if the user is already an administrator or is the primary administrator
     * 401 handled by Spring security
     * 403 handled by Spring security
     * 406 not acceptable if the user selected does not exist
     */
    @PutMapping("/businesses/{id}/makeAdministrator")
    public ResponseEntity<HttpStatus> makeUserAdminBusiness(@RequestBody JSONObject user, @PathVariable long id,
                                                            @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        // If no session id is provided, then the current user is not logged in. (Important for controller tests)
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user must be logged in to make this request.");
        }

        long requestedUserId = user.getAsNumber("userId").longValue();
        User currentUser = userRepository.findUserBySessionTicket(sessionId);

        int result = businessService.makeUserAdminBusiness(requestedUserId, id, currentUser);

        if (result == -1) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user.");
        }
        if (result == -2) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only a Primary Administrator can give a user administrator rights to a business.");
        }
        if (result == -3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The individual user may already be an administrator of the business or is the primary administrator.");
        }

        log.info(String.format("Gave business administrator to user with ID %d", requestedUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Removes an individual as an administrator for a business.
     * Will not work for businesses or non Global or Primary Administrator types of users.
     *
     * @param user JSON object containing the id of the selected user
     * @param id id of the business from the path
     * @param sessionId the session id of the logged in user
     * @return 200 ok if the user was removed successfully as an administrator from the business
     * 400 bad request if the user is not an administrator or is the primary administrator
     * 401 handled by Spring security
     * 403 handled by Spring security
     * 406 not acceptable if the user selected does not exist
     */
    @PutMapping("/businesses/{id}/removeAdministrator")
    public ResponseEntity<HttpStatus> removeUserAdminBusiness(@RequestBody JSONObject user, @PathVariable long id,
                                                              @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        // if no sessionId is provided then the current user isn't logged in
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request.");
        }

        long requestedUserId = user.getAsNumber("userId").longValue();
        User currentUser = userRepository.findUserBySessionTicket(sessionId);

        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user.");
        }

        int result = businessService.removeUserAdminBusiness(requestedUserId, id, currentUser);

        if (result == -1) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user.");
        }
        if (result == -2) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only a Primary Administrator or a Global Admin can give a user administrator rights to a business.");
        }
        if (result == -3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The individual user may not be an administrator of the business or is the Primary Administrator.");
        }

        log.info(String.format("Removed business administrator from user with ID %d", requestedUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Checks if the user is logged in and then calls the service class to retrieve the search query result
     *
     * @param specs the search query
     * @param pageNumber int value of the page
     * @param pageSize int value of the page size
     * @param sortBy String containing sort by option
     * @return 200 if the search query result was retrieved successfully with data
     * 401 if the user is not authorized (done through SPRING security)
     */
    @GetMapping("/businesses")
    public ResponseEntity<PaginationInfo<BusinessResponse>> searchBusiness(@SearchSpec(caseSensitiveFlag = false) Specification<Business> specs,
                                                                   @RequestParam(name = "page", defaultValue = "1") Integer pageNumber,
                                                                   @RequestParam(name = "size", defaultValue = "10") Integer pageSize,
                                                                   @RequestParam(name = "sortBy", defaultValue = "NAME_DESC") String sortBy,
                                                                   @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info("[GET /business] Trying to get businesses that respond to the provided search query");

        // if no sessionId is provided then the current user isn't logged in
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request.");
        }

        return new ResponseEntity<>(businessService.businessSearch(specs, pageNumber, pageSize, sortBy), HttpStatus.OK);
    }

    /**
     * Deletes a single business
     *
     * @return OK or Unauthorised Http status
     */
    @DeleteMapping("/businesses/{id}")
    public ResponseEntity<HttpStatus> deleteBusiness(@PathVariable Long id,
                                                     @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[DELETE /businesses/{id}] Trying to delete business with ID: %d", id));

        // If no session id is provided, then the current user is not logged in. (Important for controller tests)
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user must be logged in to make this request.");
        }

        businessService.deleteBusiness(id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

}
