package org.seng302.main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test functionality of checking and adding DGAA to UserRepository
 */
@SpringBootTest
class DGAAHelperTests {

    @Autowired
    DGAAHelper dgaaHelper;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void init() {
        userRepository.deleteAll();
    }

    /**
     * Test that checkDGAAExists method in dgaaHelper returns false when DGAA does not exists
     */
    @Test
    @Transactional
    void testCheckDGAADoesNotExist() {
        userRepository.deleteByUserRole("ROLE_DEFAULT_ADMIN");
        assertFalse(dgaaHelper.checkDGAAExists());
    }

    /**
     * Test that adding a dgaa using dgaaHelper.addDGAA() works by using dgaaHelper.checkDGAAExists()
     */
    @Test
    void testAddDGAA() {
        userRepository.deleteAll();
        dgaaHelper.addDGAA();
        assertTrue(dgaaHelper.checkDGAAExists());
    }

    /**
     * Check that a dgaa is added after initial delay of 2 seconds (as defined in application.properties)
     *
     * @throws InterruptedException InterruptedException
     */
    @Test
    void testPeriodCheck() throws InterruptedException {
        userRepository.deleteAll();
        TimeUnit.SECONDS.sleep(10);
        assertTrue(dgaaHelper.checkDGAAExists());
    }

}
