package org.seng302.main.controllers;

import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.seng302.main.dto.response.InventoryItemResponse;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.models.Business;
import org.seng302.main.models.InventoryItem;
import org.seng302.main.models.User;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.BusinessService;
import org.seng302.main.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Inventory Controller
 *
 * Handles all the responses
 */
@Controller
@Log4j2
public class InventoryController {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private BusinessService businessService;

    /**
     * Adds a new item, from a businesses catalogue, to a businesses inventory
     *
     * @param id id of business
     * @param sessionId the session id of the logged in user
     */
    @PostMapping("businesses/{id}/inventory")
    public ResponseEntity<JSONObject> addProductToBusinessInventory(@PathVariable Long id, @RequestBody JSONObject newItemJson,
                                                                    @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[POST /businesses/{id}/inventory] Trying to add product to business inventory with ID: %d", id));

        // If no session id is provided, then the current user is not logged in. (Important for controller tests)
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user must be logged in to make this request.");
        }

        // Get the current user using the session ticket
        User currentUser = userRepository.findUserBySessionTicket(sessionId);

        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user.");
        }

        int result = inventoryService.createInventoryItem(currentUser, newItemJson, id);

        if (result == -1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid values given!");
        }
        if (result == -2) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only an administrator of the business or a DGAA can add a product to its inventory");
        }
        if (result == -3) {
            InventoryItem inventoryItem = inventoryService.getInventoryItem(newItemJson);
            String errorMessage = inventoryService.isValidInventoryItem(inventoryItem, id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        InventoryItem inventoryItem = inventoryService.getInventoryItem(newItemJson);
        Long newItemId = inventoryService.saveItemInRepository(inventoryItem);

        JSONObject idObject = new JSONObject();
        idObject.put("inventoryItemId", newItemId);

        return new ResponseEntity<>(idObject, HttpStatus.CREATED);
    }

    /**
     * Get all items in a businesses inventory
     *
     * @param id Business ID
     * @param sessionId SessionID of the user requesting the inventory
     * @return 200 OK response code with JSON formatted inventory items
     * 401 Unauthorized
     * 403 Forbidden
     * 406 Not acceptable
     */
    @GetMapping("/businesses/{id}/inventory")
    public ResponseEntity<PaginationInfo<InventoryItemResponse>> getBusinessInventory(@PathVariable long id,
                                                                                      @RequestParam(name = "page", defaultValue = "1") Integer pageNumber,
                                                                                      @RequestParam(name = "size", defaultValue = "8") Integer pageSize,
                                                                                      @RequestParam(name = "sortBy", defaultValue = "NAME_DESC") String sortBy,
                                                                                      @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("Getting inventory from business with ID %d", id));

        // If no session id is provided, then the current user is not logged in. (Important for controller tests)
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user must be logged in to make this request.");
        }

        // If business doesn't exist
        Business business = businessRepository.findBusinessById(id);
        if (business == null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The requested route does not exist");
        }

        User currentUser = userRepository.findUserBySessionTicket(sessionId);

        // If user isn't the business admin or a global application admin
        if (!businessService.isAdminOfBusinessOrGAA(currentUser, business)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "The account performing the request is neither an administrator of the business, nor a global application admin.");
        }

        return new ResponseEntity<>(inventoryService.getInventoryItems(id, pageNumber, pageSize, sortBy), HttpStatus.OK);
    }

    /**
     * Updates an inventory item.
     *
     * @param businessId ID of the business
     * @param inventoryItemId ID of the inventory item
     * @param newItemJson InventoryItem with new details
     * @param sessionId of the logged in user
     * @return 200 if the inventory item was updated successfully
     * 400 if provided inventory item has invalid details
     * 401 if the user is not authorized (done through SPRING security)
     * 403 if the user is not an admin of the business or is not a DGAA
     */
    @PutMapping("/businesses/{businessId}/inventory/{inventoryItemId}")
    public ResponseEntity<HttpStatus> modifyInventoryItem(@PathVariable long businessId,
                                                          @PathVariable long inventoryItemId,
                                                          @RequestBody JSONObject newItemJson,
                                                          @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("Updating inventory item with ID %d", inventoryItemId));

        // If no session id is provided, then the current user is not logged in. (Important for controller tests)
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user must be logged in to make this request.");
        }

        User currentUser = userRepository.findUserBySessionTicket(sessionId);

        int result = inventoryService.updateInventoryItem(inventoryItemId, businessId, newItemJson, currentUser);

        if (result == -1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid values given!");
        }

        if (result == -2) {
            InventoryItem inventoryItem = inventoryService.getInventoryItem(newItemJson);
            String errorMessage = inventoryService.isValidInventoryItem(inventoryItem, businessId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        // If user isn't the business admin or a global application admin
        if (result == -3) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "The account performing the request is neither an administrator of the business, nor a global application admin.");
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
