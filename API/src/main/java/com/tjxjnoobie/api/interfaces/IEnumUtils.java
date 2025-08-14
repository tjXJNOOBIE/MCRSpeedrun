/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.console.Log;
import com.tjxjnoobie.api.exceptions.InvalidGameStateChangeException;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public interface IEnumUtils {


    default <T extends Enum<T>> T parseOrThrow(Class<T> enumClass, String input) {
        Objects.requireNonNull(enumClass, "Enum class cannot be null");
        Objects.requireNonNull(input, "Enum value cannot be null");

        try {
            return Enum.valueOf(enumClass, input.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            String valid = Arrays.stream(enumClass.getEnumConstants())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));

            String error = "Invalid " + enumClass.getSimpleName() + " value: '" + input + "'. " +
                    "Expected one of: " + valid;

            Log.warn(error);
            throw new InvalidGameStateChangeException(input, enumClass);
        }
    }
}
