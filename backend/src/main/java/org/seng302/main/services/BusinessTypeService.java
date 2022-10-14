package org.seng302.main.services;

/**
 * Create enum data type with fixed set of given business type constants. Allows us to easily add business types during
 * development
 */
public enum BusinessTypeService {

    ACCOMMODATION("Accommodation"),
    FOOD_SERVICES("Food Services"),
    RETAIL("Retail Trade"),
    CHARITABLE("Charitable organisation"),
    NON_PROFIT("Non-profit organisation");

    private final String type;

    BusinessTypeService(final String type) {
        this.type = type;
    }

    /**
     * A methods that checks if the given type is a valid type
     *
     * @param checkType the given business type
     * @return true if valid
     */
    public static boolean isValidType(String checkType) {
        for (BusinessTypeService type : BusinessTypeService.values()) {
            if (type.toString().equals(checkType)) {
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
