package org.seng302.main.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Singleton date util class to help with formatting dates
 */
public class DateUtil {

    /**
     * Singleton pattern
     */
    private static DateUtil instance;

    public static DateUtil getInstance() {
        if (instance == null) {
            instance = new DateUtil();
        }
        return instance;
    }

    private DateTimeFormatter inputFormatterOne;
    private DateTimeFormatter inputFormatterTwo;
    private DateTimeFormatter outputFormatter;

    /**
     * Private constructor
     */
    private DateUtil() {
        inputFormatterOne = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        inputFormatterTwo = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyy", Locale.ENGLISH);
    }

    /**
     * Convert string into date of format `yyyy-MM-dd'T'HH:mm:ss.SSS'Z'`
     *
     * @param dateString Date string
     * @return LocalDate object
     */
    public LocalDate dateFromString(String dateString) {
        try {
            return LocalDate.parse(dateString, inputFormatterOne);
        } catch (DateTimeParseException exceptionOne) {
            try {
                return LocalDate.parse(dateString, inputFormatterTwo);
            } catch (DateTimeParseException exceptionTwo) {
                return LocalDate.parse(dateString);
            }
        }
    }

    /**
     * Convert LocalDate into string representation
     *
     * @param dateObj LocalDate object
     * @return String representation
     */
    public String stringFromDate(LocalDate dateObj) {
        return outputFormatter.format(dateObj);
    }
}
