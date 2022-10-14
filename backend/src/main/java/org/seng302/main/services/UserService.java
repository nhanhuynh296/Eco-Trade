package org.seng302.main.services;

import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.dto.response.UserResponse;
import org.seng302.main.dto.responsefactory.ResponseFactory;
import org.seng302.main.helpers.AdminRoles;
import org.seng302.main.models.User;
import org.seng302.main.repository.AddressRepository;
import org.seng302.main.repository.UserImageRepository;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * User Service that communicates with User Repo
 */
@Service
@Log4j2
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private UserImageRepository userImageRepository;

    @Autowired
    protected AuthenticationManager authenticationManager;

    private ResponseFactory responseFactory = new ResponseFactory();

    /**
     * Creates a new user, encrypts the password
     *
     * @param newUser user details
     * @param request http request
     * @return user JSON object
     */
    public JSONObject createUser(User newUser, HttpServletRequest request) {
        String newEmail = newUser.getEmail();
        String newUserPassword = newUser.getPassword();

        // Encrypt and set password
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newUserPassword);
        newUser.setPassword(encodedPassword);

        // Set date created and user role
        newUser.setCreated(LocalDate.now());
        newUser.setUserRole("ROLE_USER");

        // Save the user
        newUser.setHomeAddress(addressRepository.save(newUser.getHomeAddress()));
        User savedUser = userRepository.save(newUser);
        authWithAuthManager(request, newEmail, newUserPassword);

        // Create userId JSON object
        JSONObject entity = new JSONObject();
        entity.put("userId", savedUser.getId());

        return entity;
    }

    /**
     * If getUser function is called with only an idea and not the searchingForSelf param which is required
     * in the below function then call the getUser function defined below with searchingForSelf
     * passed in as false.
     *
     * @param id The id of the user being searched for
     * @return A JSONObject representing the user being searched for
     */
    public UserResponse getUser(Long id, Boolean searchingForSelf) {
        User user = userRepository.findUserById(id);

        if (user == null) {
            return null;
        }

        return responseFactory.getUserResponse(user, searchingForSelf, true);
    }

    /**
     * Find all admins
     *
     * @return List of users with admin role (DGAA or GAA)
     */
    public List<User> findAllAdmins() {
        List<User> userList = new ArrayList<>();
        userList.addAll(userRepository.findByUserRole(AdminRoles.ROLE_DEFAULT_ADMIN.name()));
        userList.addAll(userRepository.findByUserRole(AdminRoles.ROLE_ADMIN.name()));
        return userList;
    }

    /**
     * Updates the user using the provided id.
     * Id, user role, and session is are transferred over to the updated user.
     *
     * @param user updated user information
     * @param id of the user
     * @param hashNewPassword Hash the new password or not
     */
    public User updateUser(User user, long id, boolean hashNewPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User currentUser = userRepository.findUserById(id);

        if (hashNewPassword) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(user.getPassword());
        }

        user.setId(id);
        user.setUserRole(currentUser.getUserRole());
        user.setSessionTicket(currentUser.getSessionTicket());
        user.setCreated(currentUser.getCreated());
        user.getHomeAddress().setId(currentUser.getHomeAddress().getId());

        return userRepository.save(user);
    }

    /**
     * Updates the countryForCurrency of the given user in the repository
     *
     * @param user User
     * @param countryForCurrency Country currency
     * @return User
     */
    public User updateUserCountryForCurrency(User user, String countryForCurrency) {
        user.setCountryForCurrency(countryForCurrency);
        return userRepository.save(user);
    }

    /**
     * Retrieves the search query result for users in a paginated form with total pages and elements values.
     * If the user is an admin the default admin is also returned by the search query, otherwise, it's removed.
     *
     * @param specs the user search query
     * @param pageNumber int value of the page
     * @param pageSize int value of the page size
     * @return PaginationInfo<UserResponse> of all the users in the page and number of total pages and elements
     */
    public PaginationInfo<UserResponse> userSearch(Specification<User> specs, User currentUser, int pageNumber, int pageSize, String sortBy) {
        Pageable pageRequest;

        Sort sortingAttributes = getSortType(sortBy);
        if (sortingAttributes != null) {
            pageRequest = PageRequest.of(pageNumber, pageSize, sortingAttributes);
        } else {
            pageRequest = PageRequest.of(pageNumber, pageSize);
        }

        Page<User> page = userRepository.findAll(Specification.where(specs), pageRequest);
        List<User> users = new ArrayList<>(page.getContent());

        if (currentUser != null && !currentUser.getUserRole().equals(AdminRoles.ROLE_DEFAULT_ADMIN.name())) {
            users.removeIf(user -> user.getUserRole().equals(AdminRoles.ROLE_DEFAULT_ADMIN.name()));
        }

        int totalPages = page.getTotalPages();
        int totalElements = Math.toIntExact(page.getTotalElements());

        return new PaginationInfo<>(responseFactory.getUserResponses(users, false, false), totalPages, totalElements);
    }

    /**
     * Get sort options for searching for users
     *
     * @param sortBy Sort by string
     * @return Sort object to sort users by specified attribute
     */
    private Sort getSortType(String sortBy)
    {
        Sort sort;
        switch (sortBy) {
            case "FIRST_ASC":
                sort = Sort.by(Sort.Order.asc("firstName").ignoreCase(), Sort.Order.desc("id"));
                break;
            case "FIRST_DESC":
                sort = Sort.by(Sort.Order.desc("firstName").ignoreCase(), Sort.Order.desc("id"));
                break;
            case "LAST_ASC":
                sort = Sort.by(Sort.Order.asc("lastName").ignoreCase(), Sort.Order.desc("id"));
                break;
            case "LAST_DESC":
                sort = Sort.by(Sort.Order.desc("lastName").ignoreCase(), Sort.Order.desc("id"));
                break;
            case "NICK_ASC":
                sort = Sort.by(Sort.Order.asc("nickname").ignoreCase(), Sort.Order.desc("id"));
                break;
            case "NICK_DESC":
                sort = Sort.by(Sort.Order.desc("nickname").ignoreCase(), Sort.Order.desc("id"));
                break;
            case "COUNT_ASC":
                sort = Sort.by(Sort.Order.asc("homeAddress.country").ignoreCase(), Sort.Order.desc("id"));
                break;
            case "COUNT_DESC":
                sort = Sort.by(Sort.Order.desc("homeAddress.country").ignoreCase(), Sort.Order.desc("id"));
                break;
            case "CITY_ASC":
                sort = Sort.by(Sort.Order.asc("homeAddress.city").ignoreCase(), Sort.Order.desc("id"));
                break;
            case "CITY_DESC":
                sort = Sort.by(Sort.Order.desc("homeAddress.city").ignoreCase(), Sort.Order.desc("id"));
                break;
            default:
                sort = null;
        }

        return sort;
    }

    /**
     * Makes the provided user an admin
     *
     * @param requestedUserId the id of the requested user
     * @param user User instance of the current user
     * @return int -1 if the current user is not a DGAA
     * int -2 if the requested user is not in the database
     * int 0 if no errors
     */
    public int makeAdmin(long requestedUserId, User user) {
        if (!isApplicationAdmin(user)) {
            return -1;
        }

        User requestedUser = userRepository.findUserById(requestedUserId);

        if (requestedUser == null) {
            return -2;
        }

        userRepository.updateUserToAdmin(requestedUserId);
        return 0;
    }

    /**
     * Revokes admin from a user
     *
     * @param requestedUserId requested user id
     * @param user User instance pf the current user
     * @return int -1 if the current user is not a DGAA
     * int -2 if the requested user is not in the database
     * int -3 if the requested user is a DGAA
     * int 0 if no errors
     */
    public int revokeAdmin(long requestedUserId, User user) {
        if (!isApplicationAdmin(user)) {
            return -1;
        }

        User requestedUser = userRepository.findUserById(requestedUserId);

        if (requestedUser == null) {
            return -2;
        }
        if (user.getId().equals(requestedUser.getId())) {
            return -3;
        }

        userRepository.updateUserToRoleUser(requestedUserId);
        return 0;
    }

    /**
     * Checks if the user is a DGAA
     *
     * @param user the current user
     * @return boolean if is a DGAA
     */
    public boolean isDGAA(User user) {
        return user != null && user.getUserRole().equals("ROLE_DEFAULT_ADMIN");
    }

    /**
     * Checks if the user is a GAA
     * Note: DGAA is a superset of GAA, and so is counted as true
     *
     * @param user the current user
     * @return boolean if is a GAA
     */
    public boolean isApplicationAdmin(User user) {
        return user != null &&
                (user.getUserRole().equals("ROLE_ADMIN") || isDGAA(user));
    }

    /**
     * Authenticates a user using Spring Security
     */
    public void authWithAuthManager(HttpServletRequest request, String username, String password) {
        log.info("Authenticating with auth manager");
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        authToken.setDetails(new WebAuthenticationDetails(request));

        Authentication authentication = authenticationManager.authenticate(authToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Sets the primary image for the given user
     *
     * @param id of user
     * @param imageId image id to store
     */
    public void setPrimaryImage(Long id, Long imageId) {
        userRepository.updateUserPrimaryImage(id, imageId);
    }

}
