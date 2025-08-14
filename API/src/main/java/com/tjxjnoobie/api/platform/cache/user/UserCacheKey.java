/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.cache.user;

import com.tjxjnoobie.api.platform.cache.enums.CacheDomain;
import com.tjxjnoobie.api.platform.cache.enums.CacheSource;
import com.tjxjnoobie.api.platform.cache.enums.CacheType;
import com.tjxjnoobie.api.platform.cache.enums.CacheVersion;
import com.tjxjnoobie.api.platform.cache.interfaces.CacheKey;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class UserCacheKey<K> implements CacheKey<K> {

    private final K key;
    private final CacheType cacheType;
    private final CacheDomain cacheDomain;
    private final CacheSource source; //changed
    private final CacheVersion version;
    private final long createdAt;
    private final long expirationTime;
    private final int hashCode;
    private final AtomicInteger accessCount;





    public UserCacheKey(K key, CacheType cacheType, CacheDomain cacheDomain, CacheSource source, CacheVersion version, long expirationTime, AtomicInteger accessCount) {
        this.key = key;
        this.cacheType = cacheType;
        this.source = source;
        this.version = version;
        this.createdAt = System.currentTimeMillis();
        this.expirationTime = expirationTime;
        this.accessCount = accessCount;
        this.hashCode = computeHashCode(); // Cache it up front
        this.cacheDomain = cacheDomain;
    }

    @Override
    public Object getUserCacheKey() {
        return key;
    }

    @Override
    public CacheType getCacheType() {
        return cacheType;
    }
    public CacheDomain getCacheDomain() {
        return cacheDomain;
    }
    private int computeHashCode() {
        return Objects.hash(key, cacheType, cacheDomain, version, key, source);
    }
    public boolean isExpired() {
        return isExpired(System.currentTimeMillis());
    }

    public boolean isExpired(long currentTime) {
        return currentTime > expirationTime;
    }

    public int incrementAccess() {
        return accessCount.incrementAndGet();
    }

    public int getAccessCount() {
        return accessCount.get();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public CacheVersion getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CacheKey)) return false;
        CacheKey that = (CacheKey) o;
        return Objects.equals(key, that.getUserCacheKey()) && cacheType == that.getCacheType();
    }



    @Override
    public int hashCode() {
        return Objects.hash(key, cacheType);
    }
    @Override
    public String toString() {
        return "UserCacheKey{" +
                "key=" + key +
                ", cacheType=" + cacheType +
                ", cacheDomain=" + cacheDomain +
                ", version=" + version +
                ", ownerId=" + key +
                ", source=" + source +
                ", accessCount=" + accessCount +
                ", expirationTime=" + expirationTime +
                ", createdAt=" + createdAt +
                '}';
    }
}

