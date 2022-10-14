package org.seng302.main.security;

import org.seng302.main.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Responsible for adding headers to the built-in Spring Security logout.
 * Without these a Cors error will occur client-side.
 */
public class CustomLogout implements LogoutHandler {

    private final UserRepository userRepo;

    public CustomLogout(UserRepository userRepo) {
        super();
        this.userRepo = userRepo;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // NOTE: Access-Control-Allow-Origin MUST be http://localhost:9500 in order for cookies to work
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9500");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH, HEAD");
        response.setHeader("Access-Control-Max-Age", "6000");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, X-authorization, Content-Type, Accept");  // X-authorization is specified by the front-end so must be here
        if (userRepo != null && authentication != null) {
            userRepo.setSessionIdByEmail(null, authentication.getName());
        }
    }

}
