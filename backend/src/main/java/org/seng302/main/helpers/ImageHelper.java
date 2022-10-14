package org.seng302.main.helpers;

import java.util.List;

/**
 * Class for helping deal with images
 */
public class ImageHelper {

    /**
     * Add private constructor to avoid explicit public constructor
     */
    private ImageHelper() {}

    /**
     * List of valid filetypes for images
     */
    public static final List<String> validFiletypes = List.of("jpg", "jpeg", "png", "webp", "tiff", "bmp");

}
