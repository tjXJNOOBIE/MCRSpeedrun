package com.tjxjnoobie.interfaces;

import org.bukkit.World;
import org.bukkit.Location;

/**
 * Interface for world management operations
 */
public interface IWorldManager {
    
    /**
     * Creates a new world
     * @param worldName The name of the world
     * @param environment The world environment
     * @return The created world
     */
    World createWorld(String worldName, World.Environment environment);
    
    /**
     * Creates a world with a specific seed
     * @param worldName The name of the world
     * @param environment The world environment
     * @param seed The world seed
     * @return The created world
     */
    World createWorldFromSeed(String worldName, World.Environment environment, long seed);
    
    /**
     * Loads an existing world
     * @param worldName The name of the world to load
     * @return The loaded world or null if not found
     */
    World loadWorld(String worldName);
    
    /**
     * Unloads a world
     * @param worldName The name of the world to unload
     * @return true if successfully unloaded
     */
    boolean unloadWorld(String worldName);
    
    /**
     * Deletes a world from disk
     * @param worldName The name of the world to delete
     * @return true if successfully deleted
     */
    boolean deleteWorld(String worldName);
    
    /**
     * Checks if a world exists
     * @param worldName The name of the world to check
     * @return true if the world exists
     */
    boolean worldExists(String worldName);
    
    /**
     * Gets a world by name
     * @param worldName The name of the world
     * @return The world or null if not found
     */
    World getWorld(String worldName);
    
    /**
     * Gets the spawn location of a world
     * @param worldName The name of the world
     * @return The spawn location
     */
    Location getWorldSpawn(String worldName);
    
    /**
     * Sets the spawn location of a world
     * @param worldName The name of the world
     * @param location The spawn location to set
     */
    void setWorldSpawn(String worldName, Location location);
    
    /**
     * Copies a world
     * @param sourceWorldName The source world name
     * @param targetWorldName The target world name
     * @return true if successfully copied
     */
    boolean copyWorld(String sourceWorldName, String targetWorldName);
    
    /**
     * Gets all loaded world names
     * @return Array of world names
     */
    String[] getLoadedWorlds();
    
    /**
     * Saves a world
     * @param worldName The name of the world to save
     */
    void saveWorld(String worldName);
    
    /**
     * Saves all worlds
     */
    void saveAllWorlds();

    void saveWorldSpawn(String gameTypeText, String worldName, double x, double y, double z, float pitch, float yaw);

    void setIsSpawn(int i, String worldName);
}