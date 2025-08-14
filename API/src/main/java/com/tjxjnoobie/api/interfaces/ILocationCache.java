package com.tjxjnoobie.api.interfaces;

import org.bukkit.Location;

import java.util.UUID;

/**
 * Interface for location cache management
 */
public interface ILocationCache {
    
    /**
     * Caches a player's location
     * @param playerId The player's UUID
     * @param location The location to cache
     */
    void cacheLocation(UUID playerId, Location location);
    
    /**
     * Gets a cached location for a player
     * @param playerId The player's UUID
     * @return The cached location or null if not found
     */
    Location getCachedLocation(UUID playerId);
    
    /**
     * Removes a location from cache
     * @param playerId The player's UUID
     */
    void removeLocation(UUID playerId);
    
    /**
     * Caches a named location
     * @param name The location name
     * @param location The location to cache
     */
    void cacheNamedLocation(String name, Location location);
    
    /**
     * Gets a named location from cache
     * @param name The location name
     * @return The cached location or null if not found
     */
    Location getNamedLocation(String name);
    
    /**
     * Removes a named location from cache
     * @param name The location name
     */
    void removeNamedLocation(String name);
    
    /**
     * Gets all cached player locations
     * @return Map of player UUIDs to locations
     */
    java.util.Map<UUID, Location> getAllPlayerLocations();
    
    /**
     * Gets all named locations
     * @return Map of names to locations
     */
    java.util.Map<String, Location> getAllNamedLocations();
    
    /**
     * Clears all cached locations
     */
    void clearCache();
    
    /**
     * Clears all player locations
     */
    void clearPlayerLocations();
    
    /**
     * Clears all named locations
     */
    void clearNamedLocations();
    
    /**
     * Gets the cache size
     * @return Number of cached locations
     */
    int getCacheSize();
    
    /**
     * Checks if a location is cached for a player
     * @param playerId The player's UUID
     * @return true if location is cached
     */
    boolean hasLocation(UUID playerId);
    
    /**
     * Checks if a named location is cached
     * @param name The location name
     * @return true if location is cached
     */
    boolean hasNamedLocation(String name);

     Location getSpawn();



    void loadLocationCache();

    void removeLocationCache(String worldName);
}