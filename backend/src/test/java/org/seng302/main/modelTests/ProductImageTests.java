package org.seng302.main.modelTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.models.ProductImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test image entities
 */
class ProductImageTests {

    ProductImage image;
    String validImageFilename = "~/images/image1.png";
    String validImageThumbFilename = "~/thumbs/thumb1.png";

    /**
     * Creates new image object with valid attributes
     */
    @BeforeEach
    public void init() {
        image = new ProductImage(validImageFilename, validImageThumbFilename);
    }

    /**
     * Test getting and setting details
     */
    @Test
    void testSetterGetters() {
        assertEquals(image.getFilename(), validImageFilename);
        assertEquals(image.getThumbnailFilename(), validImageThumbFilename);
        image.setImageFilename(null);
        image.setImageThumbnail(null);
        assertNull(image.getFilename());
        assertNull(image.getThumbnailFilename());
    }

}
