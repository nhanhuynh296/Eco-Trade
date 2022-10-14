package org.seng302.main.helpers;

import org.seng302.main.models.User;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ControllerHelper {

    @Autowired
    UserRepository userRepository;

    /**
     * Method to check if the user is logged in
     * @param sessionId - of the logged in user
     * @throws ResponseStatusException - 400 if user does note exist, 401 if user is not logged in
     */
    public User authorizeUser(String sessionId) {
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request");
        }

        // Get the current user using the session ticket
        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no user for this session ID!");
        }
        return currentUser;
    }
}
