package org.seng302.main.controllers;

import com.sipios.springsearch.anotation.SearchSpec;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.seng302.main.dto.response.ListingResponse;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.models.Business;
import org.seng302.main.models.Listing;
import org.seng302.main.models.User;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.ListingRepository;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Listing Controller
 *
 * Handles all the responses
 */
@Controller
@Log4j2
public class ListingController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingService listingService;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private ListingRepository listingRepository;

    private final String nonExistantUserMessage = "There is no user for this session ID!";

    /**
     * Adds a new item to a business' Listings
     *
     * @param id of business
     * @param sessionId the session id of the logged in user
     */
    @PostMapping(path = "businesses/{id}/listings")
    public ResponseEntity<JSONObject> addListingToBusiness(@PathVariable Long id, @RequestBody JSONObject newListing,
                                                           @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String
                .format("[POST /businesses/{id}/listings] Trying to add a listing to business with ID: %d",
                        id));

        // If no session id is provided, then the current user is not logged in
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "A user has to be logged in to make this request");
        }

        // Get the current user using the session ticket
        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    nonExistantUserMessage);
        }

        Business currentBusiness = businessRepository.findBusinessById(id);
        if (currentBusiness == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "There is no business for this ID!");
        }

        int result = listingService.isValidListing(currentUser, newListing, id);

        if (result == -1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inventory item does not exist!");
        } else if (result == -2) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only a administrator of the business or a global admin can add a listing!");
        }

        Listing listing = listingService.getListingFromJSON(newListing);

        if (result == -3) {
            String errorMessage = listingService.getListingErrorCode(listing);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        Long newListingId = listingService.saveItemInRepository(listing);

        JSONObject idObject = new JSONObject();
        idObject.put("listingId", newListingId);

        return new ResponseEntity<>(idObject, HttpStatus.CREATED);
    }

    /**
     * Gets all the listings of a business
     *
     * @param id of the business
     * @param sessionId the session id of the logged in user
     * @param pageNumber number of the page
     * @param pageSize size of the page
     * @return pagination info of listing responses
     */
    @GetMapping(path = "businesses/{id}/listings")
    public ResponseEntity<PaginationInfo<ListingResponse>> getAllListingOfBusiness(@PathVariable Long id, @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId,
                                                                 @RequestParam(name = "page", defaultValue = "1") Integer pageNumber,
                                                                 @RequestParam(name = "size", defaultValue = "8") Integer pageSize,
                                                                 @RequestParam(name = "sortBy", defaultValue = "NAME_DESC") String sortBy) {
        log.info(String.format("[GET /businesses/{id}/listings] Getting all listing from business with ID: %d", id));

        // If no session id is provided, then the current user is not logged in
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "A user has to be logged in to make this request");
        }

        // Get the current user using the session ticket
        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, nonExistantUserMessage);
        }
        Business currentBusiness = businessRepository.findBusinessById(id);
        if (currentBusiness == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "There is no business for this ID!");
        }

        PaginationInfo<ListingResponse> result = listingService.getAllListingFromBusinessId(id, pageNumber, pageSize, sortBy);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Checks if the user is logged in and then calls the service class to retrieve the search query result
     *
     * @param specs the search query
     * @param pageNumber int value of the page
     * @param pageSize int value of the page size
     * @return 200 if the search query result was retrieved successfully with data
     * 401 if the user is not authorized (done through SPRING security)
     */
    @GetMapping("/listings")
    public ResponseEntity<PaginationInfo<ListingResponse>> searchListings(@SearchSpec(caseSensitiveFlag = false) Specification<Listing> specs,
                                                                          @RequestParam(name= "sortBy", defaultValue = "DATE_DESC") String sortBy,
                                                                          @RequestParam(name = "page", defaultValue = "1") Integer pageNumber,
                                                                          @RequestParam(name = "size", defaultValue = "10") Integer pageSize,
                                                                          @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info("[GET /listings] Trying to get listings that correspond to the provided search query");

        // if no sessionId is provided then the current user isn't logged in
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request.");
        }

        return new ResponseEntity<>(listingService.listingSearch(specs, sortBy, pageNumber, pageSize), HttpStatus.OK);
    }

    /**
     * Returns a ListingResponse DTO (blue-sky) given its id
     *
     * @param id of listing to retrieve
     * @param sessionId of requesting user
     * @return 200 and ListingResponse DTO if listing is found
     * 401 if session id is not provided
     * 400 if session id does not correspond to a user
     * 404 if listing with id is not found
     */
    @GetMapping("/listings/{id}")
    public ResponseEntity<ListingResponse> getOneListing(@PathVariable Long id,
                                                         @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info("[GET /listings/{id}] Trying to get listing with id " + id);

        // if no sessionId is provided then the current user isn't logged in
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request.");
        }

        if (userRepository.findUserBySessionTicket(sessionId) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, nonExistantUserMessage);
        }

        if (listingRepository.getListingById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "There is no listing with this ID!");
        }

        return new ResponseEntity<>(listingService.getOneListing(id), HttpStatus.OK);
    }


    /**
     * Buy a listing. This endpoint deletes the listing and updates the associated business' sale history
     *
     * @param id of listing to buy
     * @param sessionId of user buying the listing
     * @return 200 if listing is successfully updated
     * 401 if session id is not provided
     * 400 if there is no user for the session id
     * 406 if listing is not found
     */
    @PutMapping("/listings/{id}")
    public ResponseEntity<HttpStatus> buyListings(@PathVariable Long id,
                                                  @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {

        log.info(String.format("[PUT /listings/{id}] Trying to buy listing with id %d", id));

        // if no sessionId is provided then the current user isn't logged in
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request.");
        }

        User user = userRepository.findUserBySessionTicket(sessionId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, nonExistantUserMessage);
        }
        if (listingRepository.getListingById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "There is no listing with this ID!");
        }

        listingService.buyListing(id, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
