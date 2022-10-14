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
 * Specifies all the possible types that a notification can have
 */
public enum NotificationTagType {
    NONE(null),
    REQUIRES_ATTENTION("Requires Attention"),
    HIGH_PRIORITY("High Priority"),
    MEDIUM_PRIORITY("Medium Priority"),
    LOW_PRIORITY("Low Priority"),
    INTERESTING("Interesting");

    private static final Map<String, NotificationTagType> enumFormats = Stream.of(NotificationTagType.values())
            .collect(Collectors.toMap(s -> s.formatted, Function.identity()));

    private final String formatted;

    /**
     * Constructor to turn JSON value into Enum
     *
     * @param string String representation of enum, essentially `formatted` variable
     * @return Enum value
     */
    @JsonCreator
    public static NotificationTagType fromString(@JsonProperty("notificationTagType") String string) {
        return Optional
                .ofNullable(enumFormats.get(string))
                .orElseThrow(() -> new IllegalArgumentException(string));
    }

    /**
     * Store string representation
     *
     * @param formatted Formatted string
     */
    NotificationTagType(String formatted) {
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
