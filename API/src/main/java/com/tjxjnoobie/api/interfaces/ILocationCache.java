package com.tjxjnoobie.api.interfaces;

import org.bukkit.Location;

/**
 * Interface for location cache management
 */
public interface ILocationCache {

    default Location getSpawn(){
        return null;
    }

    default void loadLocationCache(){

    }

    default void removeLocationCache(String worldName) {

    }

}