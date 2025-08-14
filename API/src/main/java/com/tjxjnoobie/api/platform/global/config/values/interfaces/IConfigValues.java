/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.global.config.values.interfaces;

import com.tjxjnoobie.api.platform.global.config.enums.ConfigDomain;

import java.util.Map;

public interface IConfigValues<T> {



    Object getRaw(String key);

    Map<ConfigDomain, Map<String, Object>> getAll();

    boolean has(String key);

    void remove(String key);

    void clear();

    T getValue();
}
