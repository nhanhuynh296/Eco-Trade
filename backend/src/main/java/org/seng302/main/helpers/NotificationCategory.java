package org.seng302.main.helpers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ENUM for notification category
 */
public enum NotificationCategory {
    // To ensure starred items are returned first, the order matters!
    STARRED("STARRED"),
    READ("READ"),
    UNREAD("UNREAD"),
    ARCHIVED("ARCHIVED");

    private static final Map<String, NotificationCategory> enumFormats = Stream.of(NotificationCategory.values())
            .collect(Collectors.toMap(s -> s.formatted, Function.identity()));

    private final String formatted;

    /**
     * Constructor to turn JSON value into Enum
     *
     * @param string String representation of enum, essentially `formatted` variable
     * @return Enum value
     */
    @JsonCreator
    public static NotificationCategory fromString(@JsonProperty("notificationCategory") String string) {
        return Optional
                .ofNullable(enumFormats.get(string))
                .orElseThrow(() -> new IllegalArgumentException(string));
    }

    /**
     * Store string representation
     *
     * @param formatted Formatted string
     */
    NotificationCategory(String formatted) {
        this.formatted = formatted;
    }

    /**
     * Get out the formatted string value of this enum
     *
     * @return String representation
     */
    @JsonValue
    @Override
    public String toString() {
        return formatted;
    }

}
