package org.seng302.main.controllers;

import com.sipios.springsearch.anotation.SearchSpec;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.seng302.main.dto.request.CurrencyChangeRequest;
import org.seng302.main.dto.request.UserRequest;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.dto.response.UserResponse;
import org.seng302.main.models.User;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.UserImageService;
import org.seng302.main.services.UserService;
import org.seng302.main.services.UserValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

/**
 * User Controller
 *
 * Handles all the responses
 */
@Controller
@Log4j2
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserImageService userImageService;

    /**
     * Create a new User and insert it into the database. Automatically logs in user as well, thus
     * sending back a JSESSIONID cookie
     *
     * @param userRequest user dto of user object
     * @param request Request from client
     * @return A ResponseEntity with the appropriate HTTP status and body (JSON of user_id)
     */
    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@RequestBody UserRequest userRequest, HttpServletRequest request) {
        log.info("[POST /users] Trying to register a new user");

        User newUser = userRequest.getUserObject();
        // The error message shows up in response, allowing use to see the 'status' and 'error' fields.
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use.");
        }

        UserValidateService.validateUser(newUser);
        JSONObject user = userService.createUser(newUser, request);

        log.info(String.format("Registering new user with ID: %s", user.getAsString("userId")));
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    /**
     * Update a user's attributes. Validation is performed in userService and appropriate HTTP response statuses are
     * sent back on failure.
     *
     * @param id user-to-update's id
     * @param userRequest User dto
     * @param sessionId of requesting user
     * @return A ResponseEntity with the appropriate HTTP status and body (JSON of user_id)
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable long id,
                                             @RequestBody UserRequest userRequest,
                                             @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info("[PUT /users/{id}] Trying to update user with id " + id);

        User updatedUser = userRequest.getUserObject();
        log.info(updatedUser.getHomeAddress().getCountry());

        // if no sessionId is provided then the current user isn't logged in
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request.");
        }

        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "There is no user for this session Id.");
        }

        if (currentUser.getId() != id && !userService.isApplicationAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this operation.");
        }

        User updatingUser = userRepository.findUserById(id);
        if (updatingUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no user for this id.");
        }

        User duplicatedEmailUser = userRepository.findUserByEmail(updatedUser.getEmail());
        if (duplicatedEmailUser != null && duplicatedEmailUser.getId() != id) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use.");
        }

        boolean isNewPassword = true;
        if (updatedUser.getPassword() == null) {
            updatedUser.setPassword(currentUser.getPassword());
            isNewPassword = false;
        }

        UserValidateService.validateUser(updatedUser);
        updatedUser = userService.updateUser(updatedUser, id, isNewPassword);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    /**
     * Get a single user by path variable: id
     *
     * @param id id of user
     * @return JSON of user object
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id,
                                              @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info("[GET /users/{id}] Trying to get a user from ID");

        boolean searchingForSelf = false;
        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        if (currentUser != null && currentUser.getId().equals(id)) {
            searchingForSelf = true;
        }

        UserResponse user = userService.getUser(id, searchingForSelf);

        if (user == null) {
            log.debug("User with ID not found");
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "ID has no associated user");
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    /**
     * If the user's cookie is correct this will return true, if not it will be caught by the MainCORSFilter
     * (see WebSecurityConfig.java) and return a 401 Unauthorized status
     *
     * @return 200 OK Status
     */
    @GetMapping("/user/loginCheck")
    public ResponseEntity<HttpStatus> isLoggedInUser() {
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /**
     * Takes a search query as a string and returns all users in the database who match the
     * specifications in the given search query as a JSONArray.
     *
     * @return Pagination info of user responses
     */
    @GetMapping("/users")
    public ResponseEntity<PaginationInfo<UserResponse>> person(@SearchSpec(caseSensitiveFlag = false) Specification<User> specs,
                                                               @RequestParam(name = "page", defaultValue = "1") Integer pageNumber,
                                                               @RequestParam(name = "size", defaultValue = "10") Integer pageSize,
                                                               @RequestParam(name = "sortBy", defaultValue = "FIRST_DESC") String sortBy,
                                                               @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info("[GET /users/search] Trying to get users from search query");
        // if no sessionId is provided then the current user isn't logged in
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request.");
        }

        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        return new ResponseEntity<>(userService.userSearch(specs, currentUser, pageNumber, pageSize, sortBy), HttpStatus.OK);
    }

    /**
     * Updates a user's role from ROLE_USER to ROLE_ADMIN using a given user id.
     * Will not work for businesses or other types of users.
     *
     * @param id userId
     * @return 200 ok if the user has been updated,
     * 401 if access token is missing or invalid,
     * 403 if the user does not have authority for this action,
     * 406 not acceptable if the user is not in the repository
     */
    @PutMapping("/users/{id}/makeadmin")
    public ResponseEntity<HttpStatus> makeAdmin(@PathVariable long id,
                                                @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {

        // if no sessionId is provided then the current user isn't logged in
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request.");
        }

        // Retrieve the user who made the request and check if they're the default admin
        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        int result = userService.makeAdmin(id, currentUser);


        if (result == -1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only a Default Global Administrator can give a user admin rights.");
        }
        if (result == -2) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid user.");
        }

        log.info(String.format("Gave admin to user with ID %d", id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Update a user's countryForCurrency attribute.
     *
     * @param id user-to-update's id
     * @param currencyRequest Contains the country to update the users countryForCurrency to
     * @param sessionId of requesting user
     * @return A ResponseEntity with the appropriate HTTP status and body (JSON of user_id)
     */
    @PutMapping("/users/currency/{id}")
    public ResponseEntity<HttpStatus> updateUsersCurrency(@PathVariable long id,
                                                 @RequestBody CurrencyChangeRequest currencyRequest,
                                                 @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {

        log.info(String.format("[PUT /users/currency/{id}] Trying to update user %d's currency to the currency of %s", id, currencyRequest.country));

        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        if (sessionId.equals("None") || currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request.");
        }

        User updatingUser = userRepository.findUserById(id);
        if (updatingUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no user for this id.");
        }

        if (currentUser.getId() != id && !userService.isApplicationAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this operation.");
        }

        userService.updateUserCountryForCurrency(updatingUser, currencyRequest.getCountry());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Updates a user's role from ROLE_ADMIN to ROLE_USER using a given user id.
     * Will not work for businesses or other types of users.
     *
     * @param id user Id
     * @return 200 ok if the user has been updated,
     * 401 if access token is missing or invalid,
     * 403 if the user does not have authority for this action,
     * 406 not acceptable if the user is not in the repository,
     * 409 if the dgaa is attempting to revoke their permission
     */
    @PutMapping("/users/{id}/revokeadmin")
    public ResponseEntity<HttpStatus> revokeAdmin(@PathVariable long id,
                                                  @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {

        // if no sessionId is provided then the current user isn't logged in
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request.");
        }

        // Retrieve the user who made the request and check if they're the default admin
        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        int result = userService.revokeAdmin(id, currentUser);

        if (result == -1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only a Default Global Administrator can revoke admin rights.");
        }
        if (result == -2) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user.");
        }
        if (result == -3) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "DGAA cannot revoke admin privileges from themself.");
        }

        log.info(String.format("Revoked admin from user with ID %d", id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Stores a given profile image and its (created) thumbnail. If this is the first image, it is automatically made
     * primary.
     *
     * @param id of user whose image store is being added to
     * @param multipartImage image file
     * @param sessionId of user sending request
     * @return 201 if image is created
     * 401 if no session id is provided
     * 400 if either the 'id' does not relate to a user or there is something wrong with the file being sent
     * 403 if the requesting user is not an admin and not the same as the user being updated
     */
    @PostMapping("/users/{id}/images")
    public ResponseEntity<JSONObject> postProfileImage(@PathVariable Long id,
                                                       @RequestParam("file") MultipartFile multipartImage,
                                                       @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[POST /users/%d/images] Trying to add image to user with id %d", id, id));
        // Get the current user using the session ticket
        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user must be logged in to do this.");
        }

        if (multipartImage == null || multipartImage.getOriginalFilename() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplied image must not be null.");
        }

        int result = userImageService.validateUserImageRequest(id, currentUser, multipartImage);
        switch (result) {
            case -1:
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only an Admin or profile owner can add an image.");
            case -2:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user.");
            case -3:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplied image is invalid");
            default:
                break;
        }
        long imageId;
        try {
            imageId = userImageService.saveUserImage(id, multipartImage);
        } catch (Exception e) {
            log.warn("Failed to save image");
            log.warn(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to save image, unknown error");
        }

        JSONObject json = new JSONObject();
        json.put("imageId", imageId);

        return new ResponseEntity<>(json, HttpStatus.CREATED);
    }

    /**
     * Sets the new primary image for a user.
     *
     * @param imageId ID of the image that is being updated to primary image
     * @param sessionId Cookie session value
     * @return HTTP Response Code indicating success/failure
     */
    @PutMapping("/users/images/{imageId}/makeprimary")
    public ResponseEntity<HttpStatus> updateUserPrimaryImage(@PathVariable Long imageId,
                                                             @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[PUT /users/images/{imageId}/makeprimary]" +
                " Trying to set image with id %d as a primary image", imageId));

        // Get the current user using the session ticket
        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user must be logged in to do this");
        }

        int result = userImageService.checkImageExists(imageId);
        // Image does not exist if 1 returned
        if (result == 1) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Image does not exist");
        }

        userService.setPrimaryImage(currentUser.getId(), imageId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Takes the id of a specific user image and returns the corresponding byte array
     *
     * @param imageId Image ID
     * @return A status code and an image if successful
     */
    @GetMapping(value = "/users/images/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getUserImage(@PathVariable Long imageId, @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[GET /users/images/{imageId}] Trying to get image with image id %s", imageId));

        if (sessionId.equals("None") || userRepository.findUserBySessionTicket(sessionId) == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user must be logged in to do this");
        }

        return userImageService.sendImageFromDirectory(imageId, false);
    }

    /**
     * Takes the id of a specific user image and returns the corresponding byte array
     *
     * @param imageId Image ID
     * @return A status code and an image if successful
     */
    @GetMapping(value = "/users/images/{imageId}/thumbnail", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getUserThumbnail(@PathVariable Long imageId, @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[GET /users/images/{imageId}/thumbnail] Trying to get image with image id %s", imageId));

        if (sessionId.equals("None") || userRepository.findUserBySessionTicket(sessionId) == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user must be logged in to do this");
        }

        return userImageService.sendImageFromDirectory(imageId, true);
    }

}
