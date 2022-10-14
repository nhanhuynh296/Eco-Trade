package org.seng302.main.modelTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.models.Keyword;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test keyword entity
 */
class KeywordTests {

    private Keyword keyword;
    private String name = "apple";
    private LocalDate date = LocalDate.now();

    /**
     * Before each test a new instance of Keyword is set
     */
    @BeforeEach
    public void init() {
        keyword = new Keyword(name, date);
    }

    /**
     * Tests setters and getters for keyword entity
     */
    @Test
    void testSettersAndGetters() {
        assertEquals(keyword.getName(), name);
        assertEquals(keyword.getCreated(), date);

        keyword.setName(null);
        keyword.setCreated(null);

        assertNull(keyword.getName());
        assertNull(keyword.getCreated());
    }

}
