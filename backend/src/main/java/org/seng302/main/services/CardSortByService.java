package org.seng302.main.services;


/**
 * Create enum data type with fixed set of given card section constants. Allows us to easily add
 * card sections during development
 */
public enum CardSortByService {

    DATE_ASC("DATE_ASC"),
    DATE_DESC("DATE_DESC"),
    TITLE_AZ("TITLE_AZ"),
    TITLE_ZA("TITLE_ZA");

    private final String sortBy;

    CardSortByService(final String sortBy) {
        this.sortBy = sortBy;
    }

    /**
     * Checks if the given sorting order is valid
     *
     * @param checkSortBy the given card sorting order
     * @return true if valid else false
     */
    public static boolean isValidSortBy(String checkSortBy) {
        for (CardSortByService sortBy : CardSortByService.values()) {
            if (sortBy.toString().equals(checkSortBy)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return sortBy;
    }

}