package org.seng302.main.models;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ProductValid {

    /**
     * Add a private constructor to hide the implicit public one. (Code smell)
     */
    private ProductValid() {}

    /**
     * Error checking to ensure product to be added is valid
     *
     * @param product Product model
     *                <p>
     *                throws an error back to the function that called it if an error is found.
     */
    public static void validateProduct(Product product) {
        int maxNameLength = 70;
        int maxDescriptionLength = 250;

        if (product.getName() == null || product.getName().strip().length() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product name cannot be empty.");
        } else if (product.getName().length() > maxNameLength) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product name length exceeds the max length of " + maxNameLength + ".");
        } else if (product.getDescription().length() > maxDescriptionLength) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product description exceeds max character length of " + maxDescriptionLength + ".");
        }
    }

}
