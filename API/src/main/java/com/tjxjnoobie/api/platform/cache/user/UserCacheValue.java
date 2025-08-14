/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.cache.user;

import com.tjxjnoobie.api.platform.cache.enums.CacheType;
import com.tjxjnoobie.api.platform.cache.interfaces.CacheValue;

public class UserCacheValue<V> implements CacheValue<V> {

    private final V value;
    private final long expirationTime;
    private final CacheType cacheType;

    public UserCacheValue(V value, CacheType cacheType, long expirationTime) {
        this.value = value;
        this.expirationTime = expirationTime;
        this.cacheType = cacheType;
    }
    @Override
    public V getValue() {
        return value;
    }
    @Override
    public boolean isExpired() {
        return isExpired(System.currentTimeMillis());
    }
    @Override
    public boolean isExpired(long currentTime) {
        return currentTime > expirationTime;
    }
    @Override
    public CacheType getCacheType() {
        return cacheType;
    }

    @Override
    public String toString() {
        return "CacheValue{" +
                "value=" + value +
                ", expirationTime=" + expirationTime +
                '}';
    }
}
