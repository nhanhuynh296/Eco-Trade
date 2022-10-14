package org.seng302.main;

import lombok.extern.log4j.Log4j2;
import org.seng302.main.helpers.AdminRoles;
import org.seng302.main.models.Address;
import org.seng302.main.models.User;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Holds DGAA checking and adding logic
 */
@PropertySource("classpath:application.properties")
@Component
@Log4j2
public class DGAAHelper {

    @Autowired
    UserRepository userRepository;

    String email;
    String pass;

    /**
     * Constructor
     *
     * @param userRepository UserRepository instance to use
     */
    @Autowired
    public DGAAHelper(UserRepository userRepository, @Value("${dgaa.user}") String user, @Value("${dgaa.pass}") String pass) {
        this.userRepository = userRepository;
        this.email = user;
        this.pass = pass;
    }

    /**
     * Check if a DGAA (a user with role ROLE_DGAA exists)
     *
     * @return true if DGAA exists
     */
    public boolean checkDGAAExists() {
        log.trace("Checking DGAA exists");  // Switch to .info if needed, will spam the console however.
        List<User> users = userRepository.findByUserRole(AdminRoles.ROLE_DEFAULT_ADMIN.name());
        return !users.isEmpty();
    }

    /**
     * Adds a new User to UserRepository with role ROLE_DEFAULT_ADMIN
     */
    public void addDGAA() {
        log.info("Creating DGAA");

        /* Do not allow multiple calls to add a DGAA */
        if (checkDGAAExists()) {
            return;
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        LocalDate date = LocalDate.now();

        User dgaa = new User();
        Address validAddress1 = new Address("3/24", "Ilam Road", "Christchurch", "Canterbury", "New Zealand"
                , "90210");

        dgaa.setFirstName("DGAA");
        dgaa.setLastName("DGAA");
        dgaa.setEmail(this.email);
        dgaa.setDateOfBirth(LocalDate.of(1970, 1, 1));
        dgaa.setHomeAddress(validAddress1);
        dgaa.setPassword(passwordEncoder.encode(this.pass));
        dgaa.setCreated(date);
        dgaa.setUserRole(AdminRoles.ROLE_DEFAULT_ADMIN.name());

        userRepository.save(dgaa);
    }

}
