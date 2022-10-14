package org.seng302.main.modelTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.models.Card;
import org.seng302.main.models.Keyword;
import org.seng302.main.models.User;
import org.seng302.main.services.CardSectionService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.seng302.main.services.CardSectionService.isValidSection;

/**
 * Test card entity
 */
class CardTests {

    private Card card;
    private final User creator = new User();
    private final String section = "ForSale";
    private final LocalDateTime date = LocalDateTime.now();
    private final LocalDateTime displayPeriodEnd = LocalDateTime.now();
    private final String title = "Lada";
    private final String description = "Good car";
    private final List<Keyword> keywords = new ArrayList<>();

    /**
     * Before each test a new instance of Card is set
     */
    @BeforeEach
    public void init() {
        card = new Card(creator, section, date, displayPeriodEnd, title, description, keywords);
    }

    /**
     * Ensures business type of business is valid
     */
    @Test
    void checkValidityCardSection() {
        assertTrue(isValidSection(card.getSection()));

        card.setSection("Error");

        assertFalse(isValidSection(card.getSection()));
    }

    /**
     * Ensures all sections for a card in current enum are valid
     */
    @Test
    void checkAllCardSections() {
        for (CardSectionService section : CardSectionService.values()) {
            card.setSection(section.toString());
            assertTrue(isValidSection(card.getSection()));
        }
    }

    /**
     * Tests getters for card entity
     */
    @Test
    void testGettersCard() {
        assertEquals(creator, card.getCreator());
        assertEquals(section, card.getSection());
        assertEquals(date, card.getCreated());
        assertEquals(displayPeriodEnd, card.getDisplayPeriodEnd());
        assertEquals(title, card.getTitle());
        assertEquals(description, card.getDescription());
        assertEquals(keywords, card.getKeywords());
    }

    /**
     * Tests setters for card entity
     */
    @Test
    void testSettersCard() {
        card.setCreator(null);
        card.setSection(null);
        card.setCreated(null);
        card.setDisplayPeriodEnd(null);
        card.setTitle(null);
        card.setDescription(null);
        card.setKeywords(null);

        assertNull(card.getCreator());
        assertNull(card.getSection());
        assertNull(card.getCreated());
        assertNull(card.getDisplayPeriodEnd());
        assertNull(card.getTitle());
        assertNull(card.getDescription());
        assertNull(card.getKeywords());
    }

}
