package org.seng302.main.helperTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.seng302.main.helpers.ImageHelper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for the ImageHelper class
 */
@RunWith(SpringJUnit4ClassRunner.class)
class ImageHelperTests {

    /**
     * Test a subset of valid file extensions to see if they're accepted
     */
    @Test
    void testValidFileExtensions() {
        String[] strings = new String[]{
                "jpg",
                "JPEG",
                "PNG",
                "jpeg",
                "tiff",
                "TIFF"
        };

        for (String str : strings) {
            Assertions.assertTrue(ImageHelper.validFiletypes.contains(str.toLowerCase()));
        }
    }

    /**
     * Test a set of invalid file extensions to see if they're rejected
     */
    @Test
    void testInvalidFileExtensions() {
        String[] strings = new String[]{
                "GIF",
                "NOT_AN_EXT",
                "TXT",
                ".JPG",
                "...PNG",
                "",
        };

        for (String str : strings) {
            Assertions.assertFalse(ImageHelper.validFiletypes.contains(str));
        }
    }

    /**
     * Test a set of exceptional file extensions to see if they're rejected
     */
    @Test
    void testExceptionalStrings() {
        String[] strings = new String[]{
                ".giff",
                ".",
                "",
                "   ",
                "p E naaaa",
        };

        for (String str : strings) {
            Assertions.assertFalse(ImageHelper.validFiletypes.contains(str));
        }
    }
}
