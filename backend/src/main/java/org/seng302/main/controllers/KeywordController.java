package org.seng302.main.controllers;

import lombok.extern.log4j.Log4j2;
import org.seng302.main.dto.request.KeywordRequest;
import org.seng302.main.dto.response.KeywordResponse;
import org.seng302.main.models.Keyword;
import org.seng302.main.models.User;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Holds all keyword related API endpoints. Its corresponding service holds all the service modules
 * e.g. parameter validations for post requests.
 */
@Controller
@Log4j2
public class KeywordController {

    @Autowired
    private KeywordService keywordService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all keywords based on a specific query
     *
     * @param query Query
     * @param sessionId Users session ID
     * @return List of keywords matching query
     */
    @GetMapping("keywords/search")
    public ResponseEntity<List<KeywordResponse>> getKeywordsBySearch(@RequestParam(name = "searchQuery", defaultValue = "") String query,
                                                                     @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId)
    {
        log.info(String.format("[GET /keyword/search] Getting keywords like %s", query));

        // If no session id is provided, then the current user is not logged in, or we received a forged/invalid session ticket.
        if (sessionId.equals("None") || userRepository.findUserBySessionTicket(sessionId) == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user must be logged in to make this request.");
        }

        return new ResponseEntity<>(keywordService.getAllKeywordsLike(query), HttpStatus.OK);
    }

    /**
     * Creates a new keywords and returns appropriate response
     *
     * @param newKeyword name of new keyword
     * @param sessionId of the logged in user
     * @return 201 if the keyword is created successfully
     * 400 if there was an error with the data provided
     * 401 if the user is not logged in
     */
    @PostMapping("/keywords")
    public ResponseEntity<Keyword> createKeyword(@RequestBody KeywordRequest newKeyword,
                                                      @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {

        log.info("[POST /keywords] Trying to create a new keyword,.");

        User currentUser = userRepository.findUserBySessionTicket(sessionId);

        // No session ID or invalid session ID
        if (sessionId.equals("None") || currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request.");
        }

        keywordService.validateKeywordRequest(newKeyword);
        Keyword keyword = keywordService.createKeyword(currentUser, newKeyword);

        return new ResponseEntity<>(keyword, HttpStatus.CREATED);
    }

    /**
     * Delete a keyword
     *
     * @param keywordId name of a keyword to be delete
     * @param sessionId of logged user
     * @return 200 if delete successful
     */
    @DeleteMapping("/keywords/{id}")
    public ResponseEntity<HttpStatus> deleteKeyword(@PathVariable(name = "id") Long keywordId,
                                           @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[DELETE /keywords] Trying to delete a keyword with id %d", keywordId));

        User currentUser = userRepository.findUserBySessionTicket(sessionId);

        // No session ID or invalid session ID or not an admin
        if (sessionId.equals("None") || currentUser == null ) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access token is missing or invalid");
        } else if (!currentUser.getUserRole().equals("ROLE_DEFAULT_ADMIN") && !currentUser.getUserRole().equals("ROLE_ADMIN") ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only an admin can make this request.");
        }

        keywordService.deleteKeyword(keywordId);

        // deleted, return 200
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
