/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.java.enums;

import com.tjxjnoobie.api.java.enums.interfaces.ITypeSafeEnum;
import org.jspecify.annotations.NonNull;

/**
 * TypeSafeEnum – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @author TJ
 * @since 11/5/2025
 */
public final class TypeSafeEnum implements Comparable<TypeSafeEnum>, ITypeSafeEnum {

    private final String name;
    private final int ordinal;


    public TypeSafeEnum(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }


    @Override
    public int compareTo(@NonNull TypeSafeEnum o) {
        return 0;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
