package com.tjxjnoobie.api.exceptions;

import org.tavall.logging.Log;

import java.util.Arrays;
import java.util.stream.Collectors;

public class InvalidGameStateChangeException extends RuntimeException {
    public InvalidGameStateChangeException(String invalidValue, Class<? extends Enum<?>> enumClass) {
        super(buildMessage(invalidValue, enumClass));
    }

    private static String buildMessage(String value, Class<? extends Enum<?>> enumClass) {

        String valid = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.joining(", "));

        String message = "Invalid " + enumClass.getSimpleName() + " value: '" + value + "'. " +
                "Expected one of: " + valid;

        Log.warn("[Enum] " + message);
        return message;

    }
}
