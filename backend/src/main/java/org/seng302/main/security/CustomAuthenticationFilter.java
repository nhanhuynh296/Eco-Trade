package org.seng302.main.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.seng302.main.models.LoginForm;
import org.seng302.main.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * Filter invoked during authentication, responsible for success/failure logic flow
 */
@Log4j2
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authManager;
    private UserRepository userRepo;
    private AuthenticationSuccessHandler successHandler;
    private AuthenticationFailureHandler failureHandler;

    /**
     * Constructor
     *
     * @param authManager authentication manager sent by WebSecurityConfig
     * @param userRepo    user object repository
     */
    public CustomAuthenticationFilter(AuthenticationManager authManager, UserRepository userRepo) {
        super();
        this.authManager = authManager;
        this.userRepo = userRepo;
        this.successHandler = new SuccessHandler(userRepo);
        this.failureHandler = new FailureHandler(userRepo);
    }

    /**
     * Map request JSON body onto LoginForm object, which is then used to authenticate a user
     *
     * @param request  Request sent by client
     * @param response Response to send back to client
     * @return Authentication object on success
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) {
        // Get username & password from request
        LoginForm authRequest = null;
        try {
            authRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginForm.class);
        } catch (IOException e) {
            log.error(String.format("%s", e.getMessage()));
            log.error(String.format("%s", Arrays.toString(e.getStackTrace())));
        }

        assert authRequest != null;
        Authentication auth = new UsernamePasswordAuthenticationToken(authRequest.getEmail().toLowerCase(),
                authRequest.getPassword());

        log.info(String.format("Authentication attempt from: %s", authRequest.getEmail()));
        return authManager.authenticate(auth);

    }

    /**
     * Invoked if authentication is successful, uses SuccessHandler class for actual handling
     *
     * @param request    Request sent by client
     * @param response   Response to send back to client
     * @param chain      object provided by the servlet container to the developer giving a view into the invocation chain of a filtered request for a resource.
     * @param authResult Represents the token for an authentication request
     * @throws ServletException Defines a general exception a servlet can throw when it encounters difficulty.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws ServletException {

        log.info("Successfully authenticated a user");

        String sessionId = request.getSession().getId();
        String email = authResult.getName();

        SecurityContextHolder.getContext().setAuthentication(authResult);
        try {
            response.setHeader("Access-Control-Allow-Credentials", "true");
            // NOTE: Access-Control-Allow-Origin MUST be http://localhost:9500 in order for cookies to work
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:9500");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH, HEAD");
            response.setHeader("Access-Control-Max-Age", "6000");
            response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, X-authorization, Content-Type, Accept");  // X-authorization is specified by the front-end so must be here
            this.successHandler.onAuthenticationSuccess(request, response, authResult);
            userRepo.setSessionIdByEmail(sessionId, email);
        } catch (IOException e) {
            log.error(String.format("%s", e.getMessage()));
            log.error(String.format("%s", Arrays.toString(e.getStackTrace())));
        }
    }

    /**
     * Invoked if authentication is unsuccessful, uses FailureHandler class for actual handling
     *
     * @param request  Request sent by client
     * @param response Response to send back to client
     * @throws ServletException Defines a general exception a servlet can throw when it encounters difficulty.
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws ServletException {

        log.info("Unsuccessfully authenticated a user");

        try {
            response.setHeader("Access-Control-Allow-Credentials", "true");
            // NOTE: Access-Control-Allow-Origin MUST be http://localhost:9500 in order for cookies to work
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:9500");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH, HEAD");
            response.setHeader("Access-Control-Max-Age", "6000");
            response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, X-authorization, Content-Type, Accept");  // X-authorization is specified by the front-end so must be here
            this.failureHandler.onAuthenticationFailure(request, response, failed);
        } catch (IOException e) {
            log.error(String.format("%s", e.getMessage()));
            log.error(String.format("%s", Arrays.toString(e.getStackTrace())));
        }
    }

}
