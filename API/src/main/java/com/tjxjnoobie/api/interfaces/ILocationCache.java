package com.tjxjnoobie.api.interfaces;

import org.bukkit.Location;

/**
 * Interface for location cache management
 */
public interface ILocationCache {

    Location getSpawn();

    void loadLocationCache();

    void removeLocationCache(String worldName);

}