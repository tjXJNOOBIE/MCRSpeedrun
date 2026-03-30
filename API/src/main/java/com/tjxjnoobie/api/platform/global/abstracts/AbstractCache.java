package com.tjxjnoobie.api.platform.global.abstracts;

import com.tjxjnoobie.api.interfaces.ICacheStats;
import com.tjxjnoobie.api.platform.cache.enums.CacheDomain;
import com.tjxjnoobie.api.platform.cache.enums.CacheSource;
import com.tjxjnoobie.api.platform.cache.enums.CacheType;
import com.tjxjnoobie.api.platform.cache.enums.CacheVersion;
import com.tjxjnoobie.api.platform.cache.interfaces.CacheKey;
import com.tjxjnoobie.api.platform.cache.interfaces.CacheValue;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Abstract base class for cache implementations providing common caching functionality
 * @param <K> The key type
 * @param <V> The value type
 */
public abstract class AbstractCache<K extends CacheKey<K>, V extends CacheValue<V>> {

    protected final ConcurrentHashMap<CacheKey<K>, CacheValue<V>> cache = new ConcurrentHashMap<>();
    protected final long defaultTtlMillis;
    protected final AtomicInteger expiredEntries = new AtomicInteger();

    /**
     * Constructs an AbstractCache with a given default TTL.
     *
     * @param defaultTtl the default time-to-live value
     * @param unit       the time unit for the TTL
     */
    protected AbstractCache(long defaultTtl, TimeUnit unit) {
        this.defaultTtlMillis = unit.toMillis(defaultTtl);
    }

    /**
     * Retrieves a value from the cache using the default TTL. Loads the value using the provided function if missing.
     *
     * @param key    the cache key
     * @param loader the function to load the value if not present
     * @return the cached or newly loaded value
     */
    public CacheValue<V> get(K key, Function<CacheKey<K>, CacheValue<V>> loader) {
        return get(key, loader, defaultTtlMillis);
    }

    /**
     * Retrieves a value from the cache using a custom TTL. Loads the value using the provided function if missing.
     *
     * @param key       the cache key
     * @param loader    the function to load the value if not present
     * @param ttlMillis custom time-to-live in milliseconds
     * @return the cached or newly loaded value
     */
    public CacheValue<V> get(CacheKey<K> key, Function<CacheKey<K>, CacheValue<V>> loader, long ttlMillis) {
        CacheValue<V> entry = cache.get(key);

        if (entry != null && !entry.isExpired()) {
            return entry.getValue();
        }

        CacheValue<V> newValue = loader.apply(key);
        if (newValue != null) {
            put(key, newValue, ttlMillis);
        }

        return newValue;
    }

    /**
     * Puts a value in the cache using the default TTL.
     *
     * @param key   the cache key
     * @param value the value to cache
     */
    public void put(CacheKey<K> key, CacheValue<V> value) {
        put(key, value, defaultTtlMillis);
    }

    /**
     * Puts a value in the cache using a custom TTL.
     *
     * @param key       the cache key
     * @param value     the value to cache
     * @param ttlMillis custom time-to-live in milliseconds
     */
    public void put(CacheKey<K> key, CacheValue<V> value, long ttlMillis) {
        if (key == null || value == null) return;
        long expiresAt = System.currentTimeMillis() + ttlMillis;
        cache.put(key, createCacheValue(value, expiresAt));
    }

    /**
     * Gets a value from the cache if present and not expired.
     *
     * @param key the key to retrieve
     * @return the cached value or null if absent or expired
     */
    public V getIfPresent(K key, CacheDomain domain, CacheType type, CacheVersion version, CacheSource source) {
        CacheKey<K> fullKey = buildKey(key, domain, type, version, source);
        CacheValue<V> entry = cache.get(fullKey);
        if (entry != null && !entry.isExpired()) return entry.getValue();

        if (entry != null) {
            expiredEntries.incrementAndGet();
            cache.remove(fullKey);
        }

        return null;
    }

    /**
     * Removes a value from the cache.
     *
     * @param rawKey the key to remove
     * @return the removed value or null if not found
     */
    public V remove(K rawKey, CacheDomain domain, CacheType type, CacheVersion version, CacheSource source) {
        CacheKey<K> key = buildKey(rawKey, domain, type, version, source);
        CacheValue<V> removed = cache.remove(key);
        return removed != null ? removed.getValue() : null;
    }

    /**
     * Checks whether a key exists in the cache and is not expired.
     *
     * @param rawKey the key to check
     * @return true if the key exists and is not expired, false otherwise
     */
    public boolean containsKey(K rawKey, CacheDomain domain, CacheType type, CacheVersion version, CacheSource source) {
        return getIfPresent(rawKey, domain, type, version, source) != null;
    }

    /**
     * Clears all entries from the cache.
     */
    public void clear() {
        cache.clear();
        expiredEntries.set(0);
    }

    /**
     * Gets the current size of the cache (including expired entries).
     *
     * @return the number of entries in the cache
     */
    public int size() {
        return cache.size();
    }

    /**
     * Removes all expired entries from the cache.
     *
     * @return the number of entries removed
     */
    public int cleanupExpired() {
        int removed = 0;
        long now = System.currentTimeMillis();
        for (Iterator<Map.Entry<CacheKey<K>, CacheValue<V>>> it = cache.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<CacheKey<K>, CacheValue<V>> entry = it.next();
            if (entry.getValue().isExpired(now)) {
                it.remove();
                expiredEntries.incrementAndGet();
                removed++;
            }
        }
        return removed;
    }

    /**
     * Returns cache statistics including total, active, and expired entries.
     *
     * @return the current cache statistics
     */
    public ICacheStats getCacheStats() {
        int total = cache.size();
        int expired = expiredEntries.get();
        return create(total, total - expired, expired);
    }

    /**
     * Wraps a raw key into a full CacheKey instance.
     *
     * @param rawKey the unwrapped key
     * @return the built CacheKey
     */
    protected abstract CacheKey<K> buildKey(K rawKey,
                                            CacheDomain domain,
                                            CacheType type,
                                            CacheVersion version,
                                            CacheSource source);

    /**
     * Creates a CacheValue with a set expiration time.
     *
     * @param value          the wrapped value
     * @param expirationTime the expiration timestamp in ms
     * @return a new CacheValue instance
     */
    protected abstract CacheValue<V> createCacheValue(CacheValue<V> value, long expirationTime);
    protected abstract ICacheStats create(int total, int alive, int expired);

}