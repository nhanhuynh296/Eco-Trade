package org.seng302.main.security;

import lombok.extern.log4j.Log4j2;
import org.seng302.main.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class handles a failure to login by sending an appropriate HTTP status
 */
@Component
@Log4j2
public class FailureHandler implements AuthenticationFailureHandler {

    UserRepository userRepository;

    /**
     * Constructor
     *
     * @param userRepository User object repository
     */
    public FailureHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Handles login failure by setting the response status to 400 - bad request
     *
     * @param request   Request from client
     * @param response  Response to client
     * @param exception Abstract superclass for all exceptions related to an Authentication object being invalid for whatever reason.
     * @throws IOException if unable to write to response
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.getWriter().write("Failed login attempt, email and/or password are incorrect!");
        log.debug("Returning failed authentication status");
    }

}
