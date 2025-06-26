package com.tjxjnoobie.interfaces;

import java.util.Set;

/**
 * Interface for Redis operations
 */
public interface IRedis {
    
    /**
     * Connects to Redis server
     * @return true if connection was successful
     */
    boolean connect();
    
    /**
     * Disconnects from Redis server
     */
    void disconnect();
    
    /**
     * Checks if connected to Redis
     * @return true if connected
     */
    boolean isConnected();
    
    /**
     * Sets a string value
     * @param key The key
     * @param value The value
     */
    void set(String key, String value);
    
    /**
     * Gets a string value
     * @param key The key
     * @return The value or null if not found
     */
    String get(String key);
    
    /**
     * Sets a value with expiration
     * @param key The key
     * @param value The value
     * @param seconds Expiration time in seconds
     */
    void setex(String key, String value, int seconds);
    
    /**
     * Deletes a key
     * @param key The key to delete
     * @return true if key was deleted
     */
    boolean del(String key);
    
    /**
     * Checks if a key exists
     * @param key The key to check
     * @return true if key exists
     */
    boolean exists(String key);
    
    /**
     * Sets expiration for a key
     * @param key The key
     * @param seconds Expiration time in seconds
     * @return true if expiration was set
     */
    boolean expire(String key, int seconds);
    
    /**
     * Gets time to live for a key
     * @param key The key
     * @return TTL in seconds, -1 if no expiration, -2 if key doesn't exist
     */
    long ttl(String key);
    
    /**
     * Increments a numeric value
     * @param key The key
     * @return The new value after increment
     */
    long incr(String key);
    
    /**
     * Decrements a numeric value
     * @param key The key
     * @return The new value after decrement
     */
    long decr(String key);
    
    /**
     * Adds a member to a set
     * @param key The set key
     * @param member The member to add
     * @return true if member was added
     */
    boolean sadd(String key, String member);
    
    /**
     * Removes a member from a set
     * @param key The set key
     * @param member The member to remove
     * @return true if member was removed
     */
    boolean srem(String key, String member);
    
    /**
     * Gets all members of a set
     * @param key The set key
     * @return Set of members
     */
    Set<String> smembers(String key);
    
    /**
     * Checks if a member exists in a set
     * @param key The set key
     * @param member The member to check
     * @return true if member exists
     */
    boolean sismember(String key, String member);
    
    /**
     * Publishes a message to a channel
     * @param channel The channel name
     * @param message The message to publish
     * @return Number of subscribers that received the message
     */
    long publish(String channel, String message);
    
    /**
     * Subscribes to channels
     * @param channels The channels to subscribe to
     */
    void subscribe(String... channels);
    
    /**
     * Unsubscribes from channels
     * @param channels The channels to unsubscribe from
     */
    void unsubscribe(String... channels);
    
    /**
     * Executes a Lua script
     * @param script The Lua script
     * @param keys Script keys
     * @param args Script arguments
     * @return Script result
     */
    Object eval(String script, String[] keys, String[] args);
    
    /**
     * Flushes all data from current database
     */
    void flushdb();
    
    /**
     * Gets database size
     * @return Number of keys in database
     */
    long dbsize();
}