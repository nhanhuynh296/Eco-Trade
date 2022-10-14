package org.seng302.main.responseFactoryTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.response.UserResponse;
import org.seng302.main.dto.responsefactory.AddressResponses;
import org.seng302.main.dto.responsefactory.BusinessResponses;
import org.seng302.main.dto.responsefactory.UserResponses;
import org.seng302.main.models.Address;
import org.seng302.main.models.Business;
import org.seng302.main.models.User;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.BusinessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Test user responses
 */
@SpringBootTest
class UserResponsesTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BusinessRepository businessRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    Business testBusiness;
    User testUser;
    Address testAddressOne;
    Address testAddressTwo;

    UserResponse testUserResponse;

    /**
     * Initialising all the variables before each test
     */
    @BeforeEach
    public void init() {
        testAddressOne = new Address("3/25", "Ilam Road", "Christchurch",
                "Canterbury", "New Zealand", "90210");
        testAddressTwo = new Address("3/24", "Ilam Road", "Christchurch",
                "Canterbury", "New Zealand", "90210");

        testUser = new User("John", "Johny", "Johnson", "Johnny", "empty Bio", "email@here.com.co",
                LocalDate.of(1970, 1, 1), "0200 9020", testAddressOne, LocalDate.now(), "ROLE_USER",
                passwordEncoder.encode("pass"), null, "New Zealand");
        testUser = userRepository.save(testUser);

        testBusiness = new Business(testUser.getId(), "Some Business", "Description", testAddressTwo, BusinessTypeService.ACCOMMODATION.toString(), LocalDate.now());
        testBusiness = businessRepository.save(testBusiness);
        List<Business> businesses = new ArrayList<>();
        businesses.add(testBusiness);

        testUser.setBusinessesAdministered(businesses);

        testUserResponse = new UserResponse();
        testUserResponse.setId(testUser.getId());
        testUserResponse.setFirstName("John");
        testUserResponse.setLastName("Johnson");
        testUserResponse.setMiddleName("Johny");
        testUserResponse.setNickname("Johnny");
        testUserResponse.setBio("empty Bio");
        testUserResponse.setHomeAddress(AddressResponses.getResponse(testAddressOne, true));
        testUserResponse.setCreated(LocalDate.now());
        testUserResponse.setRole("ROLE_USER");
        testUserResponse.setPrimaryImageId(null);
        testUserResponse.setCountryForCurrency("New Zealand");
        testUserResponse.setImages(null);
        testUserResponse.setEmail("email@here.com.co");
        testUserResponse.setPhoneNumber("0200 9020");
        testUserResponse.setBusinessesAdministered(BusinessResponses.getAllResponses(businesses, true, false));
    }

    /**
     * Testing for getting user response
     */
    @Test
    void testGettingUserResponse() {
        UserResponse response = UserResponses.getResponse(testUser, true, true);

        Assertions.assertEquals(testUserResponse.getId(), response.getId());
        Assertions.assertEquals(testUserResponse.getFirstName(), response.getFirstName());
        Assertions.assertEquals(testUserResponse.getLastName(), response.getLastName());
        Assertions.assertEquals(testUserResponse.getMiddleName(), response.getMiddleName());
        Assertions.assertEquals(testUserResponse.getNickname(), response.getNickname());
        Assertions.assertEquals(testUserResponse.getBio(), response.getBio());
        Assertions.assertEquals(testUserResponse.getHomeAddress().getCountry(), response.getHomeAddress().getCountry());
        Assertions.assertEquals(testUserResponse.getCreated(), response.getCreated());
        Assertions.assertEquals(testUserResponse.getRole(), response.getRole());
        Assertions.assertEquals(testUserResponse.getEmail(), response.getEmail());
        Assertions.assertEquals(testUserResponse.getPhoneNumber(), response.getPhoneNumber());
        Assertions.assertEquals(testUserResponse.getPrimaryImageId(), response.getPrimaryImageId());
        Assertions.assertEquals(testUserResponse.getCountryForCurrency(), response.getCountryForCurrency());
        Assertions.assertEquals(testUserResponse.getImages(), response.getImages());
        Assertions.assertEquals(testUserResponse.getBusinessesAdministered().size(), response.getBusinessesAdministered().size());
    }

    /**
     * Testing for getting user responses
     */
    @Test
    void testGettingUserResponses() {
        List<UserResponse> check = new ArrayList<>();
        check.add(testUserResponse);

        List<User> users = new ArrayList<>();
        users.add(testUser);

        List<UserResponse> responses = UserResponses.getAllResponses(users, true, true);

        Assertions.assertEquals(check.size(), responses.size());
        Assertions.assertEquals(check.get(0).getId(), responses.get(0).getId());
    }

}
