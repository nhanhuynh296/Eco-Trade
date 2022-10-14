package org.seng302.main.controllers;

import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.seng302.main.dto.request.AddressRequest;
import org.seng302.main.dto.request.CardRequest;
import org.seng302.main.dto.response.CardResponse;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.helpers.ControllerHelper;
import org.seng302.main.models.Card;
import org.seng302.main.models.User;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.CardSectionService;
import org.seng302.main.services.CardService;
import org.seng302.main.services.CardSortByService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Card Controller
 *
 * Handles all the responses
 */
@Controller
@Log4j2
public class CardController {

    @Autowired
    private ControllerHelper controllerHelper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardService cardService;
    
    static final String AUTHMESSAGE = "A user has to be logged in to make this request.";

    /**
     * Creates a new card and return appropriate response
     *
     * @param newCard details
     * @param sessionId of the logged in user
     * @return 201 if the card of successfully created
     * 400 if there was an error with the data provided
     * 401 if the user is not logged in
     * 403 if a user who is not a GAA or the card user tried creating a new card
     */
    @PostMapping("/cards")
    public ResponseEntity<JSONObject> createCard(@RequestBody CardRequest newCard,
                                                 @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info("[POST /cards] Trying to create new card.");

        controllerHelper.authorizeUser(sessionId);

        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        Card result = cardService.createCard(currentUser, newCard);

        JSONObject cardObject = new JSONObject();
        cardObject.put("cardId", result.getId());
        return new ResponseEntity<>(cardObject, HttpStatus.CREATED);
    }

    /**
     * Get all cards for a given section
     *
     * @param section Section for which cards should be retrieved
     * @param sessionId SessionID of the user requesting the inventory
     * @return 200 OK response code with JSON formatted cards
     * 400 Bad Request
     * 401 Unauthorized
     */
    @GetMapping("/cards")
    public ResponseEntity<PaginationInfo<CardResponse>> getCards(@RequestParam(name = "section") String section,
                                               @RequestParam(name = "page", defaultValue = "1") Integer pageNumber,
                                               @RequestParam(name = "size", defaultValue = "8") Integer pageSize,
                                               @RequestParam(name = "sortBy", defaultValue = "DATE_DESC") String sortBy,
                                               @RequestParam(name = "country", required = false) String country,
                                               @RequestParam(name = "region", required = false) String region,
                                               @RequestParam(name = "city", required = false) String city,
                                               @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[GET /cards] Getting cards from section %s, page %s, %s per page", section, pageNumber, pageSize));

        User currentUser = userRepository.findUserBySessionTicket(sessionId);

        // No session ID or invalid session ID
        if (sessionId.equals("None") || currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, AUTHMESSAGE);
        }

        if (!CardSectionService.isValidSection(section)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "That is not a valid section!");
        }

        if (!CardSortByService.isValidSortBy(sortBy)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "That is not a valid sorting order!");
        }

        if ((country == null && region != null) || (region == null && city != null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "City requires region, region requires country!");
        }

        PaginationInfo<CardResponse> cards = cardService.getCards(section, pageNumber, pageSize, sortBy, country, region, city);
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    /**
     * Delete a card with a given Id
     *
     * @param cardId card to delete
     * @param sessionId session of user requesting
     * @return either an error or a 201 created HttpStatus
     */
    @DeleteMapping("/cards/{id}")
    public ResponseEntity<HttpStatus> deleteCards(@PathVariable(name = "id") Long cardId,
                                                  @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[DELETE /cards] Trying to delete a card with ID %s.", cardId));
        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        CardResponse card = cardService.getCard(cardId);

        if (sessionId.equals("None") || currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, AUTHMESSAGE);
        }
        if (card == null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Card with this id does not exist");
        }

        User cardUser = userRepository.findUserById(card.getCreator().getId());

        if (!cardService.isGAAorMainUser(cardUser, currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete this card");
        }

        cardService.deleteCard(cardId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get a specific cards for a given id
     *
     * @return 200 OK response code with JSON formatted cards
     * 400 Bad Request
     * 401 Unauthorized
     */
    @GetMapping("/cards/{id}")
    public ResponseEntity<CardResponse> getCard(@PathVariable(name = "id") Long cardId,
                                              @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[GET /cards/{id}] Getting card with id %s", cardId));

        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        CardResponse card = cardService.getCard(cardId);

        // No session ID or invalid session ID
        if (sessionId.equals("None") || currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, AUTHMESSAGE);
        }

        if (card == null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Card not exist");
        } else {
            return new ResponseEntity<>(card, HttpStatus.OK);
        }
    }

    /**
     * Extended the display period for a card
     *
     * @param cardId if of the selected card
     * @param sessionId session of user requesting
     * @return HTTP Status code depending on the outcome
     */
    @PutMapping("/cards/{id}/extenddisplayperiod")
    public ResponseEntity<HttpStatus> extendCardDisplayPeriod(@PathVariable(name = "id") Long cardId,
                                                              @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[DELETE /cards/{id}/extenddisplayperiod] Trying to extend display period of a card with ID %s.", cardId));
        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        CardResponse card = cardService.getCard(cardId);

        if (sessionId.equals("None") || currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, AUTHMESSAGE);
        }

        if (card == null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Card not exist");
        }

        User cardUser = userRepository.findUserById(card.getCreator().getId());

        if (!cardService.isGAAorMainUser(cardUser, currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You don't have permission to extend display duration of this card");
        }

        cardService.extendCardDisplayPeriod(card.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Gets all of the user cards
     *
     * @param userId the id of the user whos cards are being retrieved
     * @param pageNumber the current page that is loaded
     * @param pageSize the number of cards on the page
     * @param sessionId session ifd for authentication
     * @return Paginated cards
     */
    @GetMapping("/users/{id}/cards")
    public ResponseEntity<PaginationInfo<CardResponse>> getUserCards(@PathVariable(name = "id") Long userId,
                                                   @RequestParam(name = "page", defaultValue = "1") Integer pageNumber,
                                                   @RequestParam(name = "size", defaultValue = "8") Integer pageSize,
                                                   @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[GET /users/{id}/cards] Getting cards of user %s from, page %s, %s per page", userId, pageNumber, pageSize));

        User currentUser = userRepository.findUserBySessionTicket(sessionId);

        // No session ID or invalid session ID
        if (sessionId.equals("None") || currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    AUTHMESSAGE);
        }

        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "User not exist");
        }

        return new ResponseEntity<>(
                cardService.getCardsFromUser(user, pageNumber, pageSize), HttpStatus.OK);

    }

    /**
     * Searches for cards based on the filter attributes
     *
     * @param keywords list of keywords
     * @param type - String denoting whether all keywords must match or any
     * @param section of the marketplace
     * @param pageNumber number of the page
     * @param pageSize size of the page
     * @param sessionId of the logged in user
     * @return Paginated card responses
     */
    @GetMapping("/cards/search")
    public ResponseEntity<PaginationInfo<CardResponse>> searchCard(@RequestParam List<String> keywords,
                                                                   @RequestParam(name = "type", defaultValue = "and") String type,
                                                                   @RequestParam(name = "section") String section,
                                                                   @RequestParam(name = "sortBy", defaultValue = "DATE_DESC") String sortBy,
                                                                   @RequestParam(name = "country", defaultValue = "") String country,
                                                                   @RequestParam(name = "region", defaultValue = "") String region,
                                                                   @RequestParam(name = "city", defaultValue = "") String city,
                                                                   @RequestParam(name = "page", defaultValue = "1") Integer pageNumber,
                                                                   @RequestParam(name = "size", defaultValue = "8") Integer pageSize,
                                                                   @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info("[GET /cards/search] Trying to get cards that respond to the provided search query");

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setCountry(country);
        addressRequest.setRegion(region);
        addressRequest.setCity(city);
        controllerHelper.authorizeUser(sessionId);
        PaginationInfo<CardResponse> cards = cardService.cardSearch(keywords, type, section, sortBy, addressRequest, pageNumber, pageSize);

        return new ResponseEntity<>(cards , HttpStatus.OK);
    }

    /**
     * Updates a cards attributes. Validation is performed in the cardService and appropriate HTTP response statues
     * sent back on failure.
     *
     * @param id card-to-update's id
     * @param updatedCard updated card object
     * @param sessionId of requesting user
     * @return A ResponseEntity with the appropriate HTTP status and body (JSON of user_id)
     */
    @PutMapping("/cards/{id}")
    public ResponseEntity<HttpStatus> updateCard(@PathVariable long id,
                                                 @RequestBody CardRequest updatedCard,
                                                 @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info("[PUT /cards/{id}] Trying to edit card with id " + id);

        // if no sessionId is provided then the current user isn't logged in
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, AUTHMESSAGE);
        }

        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "There is no user for this session Id.");
        }

        CardResponse oldCard = cardService.getCard(id);
        if (oldCard == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no card for this id.");
        }

        User cardUser = userRepository.findUserById(oldCard.getCreator().getId());

        if (cardUser != currentUser && !cardService.isGAAorMainUser(cardUser, currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access edit this card");
        }

        cardService.updateCard(currentUser, updatedCard, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
