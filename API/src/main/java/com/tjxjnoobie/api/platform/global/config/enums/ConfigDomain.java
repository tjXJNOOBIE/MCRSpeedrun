/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.global.config.enums;

public enum ConfigDomain {
    PAPER_PLUGIN(Object.class),
    VELOCITY_PLUGIN(String.class),
    SYSTEM(String.class),
    RUNTIME(Boolean.class),
    LOG(String.class);
    private final Class<?> valueClass;

    ConfigDomain(Class<?> valueClass) {
        this.valueClass = valueClass;
    }

    public Class<?> getValueClass() {
        return valueClass;
    }
}
