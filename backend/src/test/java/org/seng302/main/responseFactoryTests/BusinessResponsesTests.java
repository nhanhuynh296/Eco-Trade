package org.seng302.main.responseFactoryTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.response.BusinessResponse;
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
 * Test business responses
 */
@SpringBootTest
class BusinessResponsesTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BusinessRepository businessRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    Business testBusiness;
    User testUser;
    Address testAddressOne;
    Address testAddressTwo;

    BusinessResponse testBusinessResponsePartial;
    BusinessResponse testBusinessResponseFull;

    /**
     * Initialising all the variables before each test
     */
    @BeforeEach
    public void init() {
        testAddressOne = new Address("3/25", "Ilam Road", "Christchurch",
                "Canterbury", "New Zealand", "90210");
        testAddressTwo = new Address("3/24", "Ilam Road", "Christchurch",
                "Canterbury", "New Zealand", "90210");

        testUser = new User("John", "", "Johnson", "Johnny", "empty Bio", "email@here.com.co",
                LocalDate.of(1970, 1, 1), "0200 9020", testAddressOne, LocalDate.now(), "ROLE_USER",
                passwordEncoder.encode("pass"), null, null);
        testUser = userRepository.save(testUser);

        testBusiness = new Business(testUser.getId(), "Some Business", "Description", testAddressTwo, BusinessTypeService.ACCOMMODATION.toString(), LocalDate.now());
        testBusiness = businessRepository.save(testBusiness);

        testBusinessResponsePartial = new BusinessResponse();
        testBusinessResponsePartial.setId(testBusiness.getId());
        testBusinessResponsePartial.setName(testBusiness.getName());
        testBusinessResponsePartial.setAddress(AddressResponses.getResponse(testAddressTwo,true));
        testBusinessResponsePartial.setBusinessType(testBusiness.getBusinessType());
        testBusinessResponsePartial.setCountryForCurrency(testBusiness.getCountryForCurrency());

        testBusinessResponseFull = new BusinessResponse();
        testBusinessResponseFull.setId(testBusiness.getId());
        testBusinessResponseFull.setName(testBusiness.getName());
        testBusinessResponseFull.setAddress(AddressResponses.getResponse(testAddressTwo,true));
        testBusinessResponseFull.setPrimaryAdministratorId(testBusiness.getPrimaryAdministratorId());
        testBusinessResponseFull.setBusinessType(testBusiness.getBusinessType());
        testBusinessResponseFull.setDescription(testBusiness.getDescription());
        testBusinessResponseFull.setCountryForCurrency(testBusiness.getCountryForCurrency());
        testBusinessResponseFull.setCreated(testBusiness.getCreated());
        testBusinessResponseFull.setAdministrators(UserResponses.getAllResponses(testBusiness.getAdministrators(), false, false));
    }

    /**
     * Testing for getting partial business response
     */
    @Test
    void testGettingPartialBusiness() {
        BusinessResponse response = BusinessResponses.getPartialResponse(testBusiness);

        Assertions.assertEquals(testBusinessResponsePartial.getId(), response.getId());
        Assertions.assertEquals(testBusinessResponsePartial.getName(), response.getName());
        Assertions.assertEquals(testBusinessResponsePartial.getBusinessType(), response.getBusinessType());
        Assertions.assertEquals(testBusinessResponsePartial.getAddress().getCountry(), response.getAddress().getCountry());
        Assertions.assertEquals(testBusinessResponsePartial.getCountryForCurrency(), response.getCountryForCurrency());
        Assertions.assertNull(response.getDescription());
        Assertions.assertNull(response.getCreated());
        Assertions.assertNull(response.getAdministrators());
    }

    /**
     * Testing for getting full business response
     */
    @Test
    void testGettingFullBusiness() {
        BusinessResponse response = BusinessResponses.getFullResponse(testBusiness, true);

        Assertions.assertEquals(testBusinessResponseFull.getId(), response.getId());
        Assertions.assertEquals(testBusinessResponseFull.getName(), response.getName());
        Assertions.assertEquals(testBusinessResponseFull.getBusinessType(), response.getBusinessType());
        Assertions.assertEquals(testBusinessResponseFull.getAddress().getCountry(), response.getAddress().getCountry());
        Assertions.assertEquals(testBusinessResponseFull.getPrimaryAdministratorId(), response.getPrimaryAdministratorId());
        Assertions.assertEquals(testBusinessResponseFull.getDescription(), response.getDescription());
        Assertions.assertEquals(testBusinessResponseFull.getCountryForCurrency(), response.getCountryForCurrency());
        Assertions.assertEquals(testBusinessResponseFull.getCreated(), response.getCreated());
        Assertions.assertEquals(testBusinessResponseFull.getAdministrators().size(), response.getAdministrators().size());
    }

    /**
     * Testing for getting partial business responses
     */
    @Test
    void testGettingListOfPartialBusinesses() {
        List<BusinessResponse> check = new ArrayList<>();
        check.add(testBusinessResponsePartial);

        List<Business> businesses = new ArrayList<>();
        businesses.add(testBusiness);

        List<BusinessResponse> responses = BusinessResponses.getAllResponses(businesses, false, false);

        Assertions.assertEquals(check.size(), responses.size());
        Assertions.assertEquals(check.get(0).getId(), responses.get(0).getId());
    }

    /**
     * Testing for getting full business responses
     */
    @Test
    void testGettingListOfFullBusinesses() {
        List<BusinessResponse> check = new ArrayList<>();
        check.add(testBusinessResponseFull);

        List<Business> businesses = new ArrayList<>();
        businesses.add(testBusiness);

        List<BusinessResponse> responses = BusinessResponses.getAllResponses(businesses, true, true);

        Assertions.assertEquals(check.size(), responses.size());
        Assertions.assertEquals(check.get(0).getId(), responses.get(0).getId());
    }

}
