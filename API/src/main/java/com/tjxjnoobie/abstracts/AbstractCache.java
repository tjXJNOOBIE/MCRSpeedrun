package com.tjxjnoobie.abstracts;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Abstract base class for cache implementations providing common caching functionality
 * @param <K> The key type
 * @param <V> The value type
 */
public abstract class AbstractCache<K, V> {
    
    protected final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    protected final long defaultTtlMillis;
    
    protected AbstractCache(long defaultTtl, TimeUnit timeUnit) {
        this.defaultTtlMillis = timeUnit.toMillis(defaultTtl);
    }
    
    /**
     * Gets a value from the cache, loading it if necessary
     * @param key The key to look up
     * @param loader Function to load the value if not in cache
     * @return The cached or loaded value
     */
    public V get(K key, Function<K, V> loader) {
        return get(key, loader, defaultTtlMillis);
    }
    
    /**
     * Gets a value from the cache with custom TTL, loading it if necessary
     * @param key The key to look up
     * @param loader Function to load the value if not in cache
     * @param ttlMillis Time to live in milliseconds
     * @return The cached or loaded value
     */
    public V get(K key, Function<K, V> loader, long ttlMillis) {
        CacheEntry<V> entry = cache.get(key);
        
        if (entry != null && !entry.isExpired()) {
            return entry.getValue();
        }
        
        // Load new value
        V value = loader.apply(key);
        if (value != null) {
            put(key, value, ttlMillis);
        }
        
        return value;
    }
    
    /**
     * Puts a value in the cache with default TTL
     * @param key The key
     * @param value The value
     */
    public void put(K key, V value) {
        put(key, value, defaultTtlMillis);
    }
    
    /**
     * Puts a value in the cache with custom TTL
     * @param key The key
     * @param value The value
     * @param ttlMillis Time to live in milliseconds
     */
    public void put(K key, V value, long ttlMillis) {
        if (key == null || value == null) {
            return;
        }
        
        long expirationTime = System.currentTimeMillis() + ttlMillis;
        cache.put(key, new CacheEntry<>(value, expirationTime));
    }
    
    /**
     * Gets a value from the cache without loading
     * @param key The key
     * @return The cached value or null if not found or expired
     */
    public V getIfPresent(K key) {
        CacheEntry<V> entry = cache.get(key);
        
        if (entry != null && !entry.isExpired()) {
            return entry.getValue();
        }
        
        // Remove expired entry
        if (entry != null) {
            cache.remove(key);
        }
        
        return null;
    }
    
    /**
     * Removes a value from the cache
     * @param key The key to remove
     * @return The removed value or null if not found
     */
    public V remove(K key) {
        CacheEntry<V> entry = cache.remove(key);
        return entry != null ? entry.getValue() : null;
    }
    
    /**
     * Checks if a key exists in the cache and is not expired
     * @param key The key to check
     * @return true if present and not expired
     */
    public boolean containsKey(K key) {
        return getIfPresent(key) != null;
    }
    
    /**
     * Clears all entries from the cache
     */
    public void clear() {
        cache.clear();
    }
    
    /**
     * Gets the current size of the cache (including expired entries)
     * @return The cache size
     */
    public int size() {
        return cache.size();
    }
    
    /**
     * Removes expired entries from the cache
     * @return The number of entries removed
     */
    public int cleanupExpired() {
        int removed = 0;
        long currentTime = System.currentTimeMillis();
        
        cache.entrySet().removeIf(entry -> {
            if (entry.getValue().isExpired(currentTime)) {
                return true;
            }
            return false;
        });
        
        return removed;
    }
    
    /**
     * Gets cache statistics
     * @return Cache statistics
     */
    public CacheStats getStats() {
        int totalEntries = cache.size();
        int expiredEntries = 0;
        long currentTime = System.currentTimeMillis();
        
        for (CacheEntry<V> entry : cache.values()) {
            if (entry.isExpired(currentTime)) {
                expiredEntries++;
            }
        }
        
        return new CacheStats(totalEntries, totalEntries - expiredEntries, expiredEntries);
    }
    
    /**
     * Cache entry wrapper
     */
    protected static class CacheEntry<V> {
        private final V value;
        private final long expirationTime;
        
        public CacheEntry(V value, long expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
        }
        
        public V getValue() {
            return value;
        }
        
        public boolean isExpired() {
            return isExpired(System.currentTimeMillis());
        }
        
        public boolean isExpired(long currentTime) {
            return currentTime > expirationTime;
        }
    }
    
    /**
     * Cache statistics
     */
    public static class CacheStats {
        private final int totalEntries;
        private final int validEntries;
        private final int expiredEntries;
        
        public CacheStats(int totalEntries, int validEntries, int expiredEntries) {
            this.totalEntries = totalEntries;
            this.validEntries = validEntries;
            this.expiredEntries = expiredEntries;
        }
        
        public int getTotalEntries() { return totalEntries; }
        public int getValidEntries() { return validEntries; }
        public int getExpiredEntries() { return expiredEntries; }
        
        @Override
        public String toString() {
            return String.format("CacheStats{total=%d, valid=%d, expired=%d}", 
                               totalEntries, validEntries, expiredEntries);
        }
    }
}