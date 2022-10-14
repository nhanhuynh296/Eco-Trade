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
 * ENUM for notification types
 */
public enum NotificationType {
    GENERAL("GENERAL"),
    CARD_DELETED("CARD_DELETED"),
    CARD_EXPIRING("EXPIRING"),
    STARRED("STARRED"),
    KEYWORD_ADDED("KEYWORD_ADDED"),
    COMMENT_RECEIVED("COMMENT_RECEIVED"),
    LIKED("LIKED"),
    BOUGHT("BOUGHT");

    private static final Map<String, NotificationType> enumFormats = Stream.of(NotificationType.values())
            .collect(Collectors.toMap(s -> s.formatted, Function.identity()));

    private final String formatted;

    /**
     * Constructor to turn JSON value into Enum
     *
     * @param string String representation of enum, essentially `formatted` variable
     * @return Enum value
     */
    @JsonCreator
    public static NotificationType fromString(@JsonProperty("notificationType") String string) {
        return Optional
                .ofNullable(enumFormats.get(string))
                .orElseThrow(() -> new IllegalArgumentException(string));
    }

    /**
     * Store string representation
     *
     * @param formatted Formatted string
     */
    NotificationType(String formatted) {
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
