package org.seng302.main.security;

import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.seng302.main.models.User;
import org.seng302.main.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * This class handles a success when logging in by sending an appropriate HTTP status
 */
@Component
@Log4j2
public class SuccessHandler implements AuthenticationSuccessHandler {

    UserRepository userRepository;

    /**
     * Constructor
     *
     * @param userRepository User object repository
     */
    public SuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Invoked by CustomAuthenticationFilter upon authentication success, uses handle() method
     *
     * @param request  Request from client
     * @param response Response to client
     * @throws IOException if unable to write to response
     */
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        handle(response, authentication);
    }

    /**
     * Handles login success
     *
     * @param response       Response to client
     * @param authentication Represents the token for an authentication request
     */
    private void handle(HttpServletResponse response, Authentication authentication) {
        User user = userRepository.findUserByEmail(authentication.getName());

        JSONObject entity = new JSONObject();
        entity.put("userId", user.getId());
        log.debug(String.format("Returning user ID: %d", user.getId()));
        response.setContentType("application/json");
        try {
            response.getWriter().write(entity.toJSONString());
        } catch (IOException e) {
            log.error(String.format("%s", e.getMessage()));
            log.error(String.format("%s", Arrays.toString(e.getStackTrace())));
        }
    }

}
