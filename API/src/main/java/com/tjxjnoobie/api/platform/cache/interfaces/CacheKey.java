/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.cache.interfaces;

import com.tjxjnoobie.api.platform.cache.enums.CacheType;

public interface CacheKey<K> {


    Object getUserCacheKey();

    CacheType getCacheType();

    // default equals() and hashCode() to use as Map key
    boolean equals(Object o);
    int hashCode();
    String toString();

}