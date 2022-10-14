package org.seng302.main.services;


/**
 * Create enum data type with fixed set of given card section constants. Allows us to easily add card sections during
 * development
 */
public enum CardSectionService {

    FOR_SALE("ForSale"),
    WANTED("Wanted"),
    EXCHANGE("Exchange");

    private final String type;

    CardSectionService(final String type) {
        this.type = type;
    }

    /**
     * A methods that checks if the given section is a valid section
     *
     * @param checkSection the given card section
     * @return true if valid
     */
    public static boolean isValidSection(String checkSection) {
        for (CardSectionService section : CardSectionService.values()) {
            if (section.toString().equals(checkSection)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return type;
    }

}