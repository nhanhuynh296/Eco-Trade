package org.seng302.main.security;

import lombok.extern.log4j.Log4j2;
import org.seng302.main.models.CustomUserDetails;
import org.seng302.main.models.User;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for UserDetails, used for checking login credentials in user repository.
 */
@Service
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    /**
     * Load a user from the user repository, return a new CustomUserDetails object if user is present.
     *
     * @param email email of user (used as username)
     * @return new CustomUserDetails Object
     * @throws UsernameNotFoundException if email is not in respository
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        Optional<User> user = userRepo.findByEmail(email);
        log.debug("Finding user by email");

        if (user.isPresent()) {
            return new CustomUserDetails(user.get());
        } else {
            throw new UsernameNotFoundException("User not found: " + email);
        }
    }

}
