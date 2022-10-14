package org.seng302.main.modelTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.models.Address;
import org.seng302.main.models.User;
import org.seng302.main.services.UserValidateService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
class UserTests {

    private User testUser;

    @BeforeEach
    void init() {
        // User with all required fields with valid attributes
        testUser = new User();
        Address testAddress = new Address("3/24", "Ilam Road", "Christchurch", "Canterbury", "New Zealand"
                , "90210");

        testUser.setFirstName("Christian");
        testUser.setMiddleName("Julian");
        testUser.setLastName("Askey");
        testUser.setEmail("christian@gmail.com");
        testUser.setHomeAddress(testAddress);
        testUser.setDateOfBirth(LocalDate.of(1970, 1, 1));
        testUser.setPassword("password");
        testUser.setCreated(LocalDate.now());
        testUser.setHomeAddress(testAddress);
    }

    /**
     * Test validityRequired method (e.g All required columns contain)
     */
    @Test
    void checkCheckRequired() {
        assertTrue(UserValidateService.checkRequired(testUser));

        //Remove required attribute
        testUser.setFirstName(null);
        assertFalse(UserValidateService.checkRequired(testUser));
    }

    /**
     * Test email check (e.g Email is of correct email format)
     */
    @Test
    void checkEmailCheck() {
        assertTrue(UserValidateService.emailCheck(testUser.getEmail()));

        // All invalid emails
        testUser.setEmail("incorrect@gmail");
        assertFalse(UserValidateService.emailCheck(testUser.getEmail()));
        testUser.setEmail("incorrect.com");
        assertFalse(UserValidateService.emailCheck(testUser.getEmail()));
        testUser.setEmail("incorrectgmailcom");
        assertFalse(UserValidateService.emailCheck(testUser.getEmail()));

        testUser.setEmail("@gmail.com");
        assertFalse(UserValidateService.emailCheck(testUser.getEmail()));
        testUser.setEmail("@.");
        assertFalse(UserValidateService.emailCheck(testUser.getEmail()));
    }

    /**
     * Test date of birth check (e.g Age is over 13 years)
     */
    @Test
    void checkDOB() {
        assertTrue(UserValidateService.ageCheck(testUser.getDateOfBirth()));

        LocalDate date = LocalDate.of(2011, 2, 10);

        testUser.setDateOfBirth(date);
        assertFalse(UserValidateService.ageCheck(testUser.getDateOfBirth()));

    }


    /**
     * Test regex matches for email
     */
    @Test
    void checkEmailRegexMatches() {
        ArrayList<String> validEmails = new ArrayList<>() {
            {
                add("a@a.com");
                add("testemail@a.com");
                add("johndoe@gmail.com");
                add("apple@apple.co.nz");
                add("google@gmail.com");
                add("about123123122@123123.com");
            }
        };

        ArrayList<String> invalidEmails = new ArrayList<>() {
            {
                add("@a.com");
                add("testemail.com");
                add("gmail.com");
                add("@");
                add("123");
                add(null);
                add("");
            }
        };

        for (String email : validEmails) {
            assertTrue(UserValidateService.emailCheck(email));
        }

        for (String email : invalidEmails) {
            assertFalse(UserValidateService.emailCheck(email));
        }
    }

    /**
     * Test regex matches for names
     */
    @Test
    void checkNameRegexMatches() {
        ArrayList<String> validNames = new ArrayList<>() {
            {
                add("Callum");
                add("John");
                add("Greg");
                add("Earl");
            }
        };

        ArrayList<String> invalidNames = new ArrayList<>() {
            {
                add("@");
                add("D'angelo");
                add("Worthington-Smythe");
                add("J1mmy");
                add("130Hi");
                add("Test Name Please Ignore");
                add("This is not valid either??");
                add(null);
                add("");
            }
        };

        for (String name : validNames) {
            assertTrue(UserValidateService.nameCheck(name));
        }

        for (String name : invalidNames) {
            assertFalse(UserValidateService.nameCheck(name));
        }
    }

}
