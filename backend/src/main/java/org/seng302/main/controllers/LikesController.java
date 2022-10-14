package org.seng302.main.controllers;

import lombok.extern.log4j.Log4j2;
import org.seng302.main.helpers.ControllerHelper;
import org.seng302.main.models.User;
import org.seng302.main.services.LikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Log4j2
public class LikesController {

    @Autowired
    ControllerHelper controllerHelper;

    @Autowired
    private LikesService likesService;

    /**
     *
     * @param id - of the listing being liked
     * @param sessionId - of the user logged in
     * @return ResponseEntity
     *              - 200 OK if successful
     *              - 400 bad request if listing doesnt exist or user does not exist
     *              - 401 unauthorized if user is not logged in
     */
    @PutMapping("listing/{id}/like")
    public ResponseEntity<String> likeListing(@PathVariable long id,
                                              @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[PUT /listing/{id}/like] Trying to like listing with id %d", id));

        User user = controllerHelper.authorizeUser(sessionId);
        likesService.addLikeToListing(id, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     *
     * @param id - of the listing being unlike
     * @param sessionId - of the user logged in
     * @return ResponseEntity
     *              - 200 OK if successful
     *              - 400 bad request if listing doesnt exist or user does not exist
     *              - 401 unauthorized if user is not logged in
     */
    @PutMapping("listing/{id}/unlike")
    public ResponseEntity<String> unlikeListing(@PathVariable long id,
                                                @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[PUT /listing/{id}/unlike] Trying to unlike listing with id %d", id));

        User user = controllerHelper.authorizeUser(sessionId);
        likesService.removeLikeFromListing(id, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
