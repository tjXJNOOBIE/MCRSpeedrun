/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.interfaces;

import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.sql.SQLException;

public interface IWorldManager {


    ;

    /**
     * Gets the global context for this world manager
     *
     * @return The global context
     */

    default ISpeedRunContext getSpeedRunContext() {
        return null;
    }


    // Override this in implementations that need context


    /**
     * Loads a world from a seed
     *
     * @param worldName The name of the world
     * @param seed      The world seed
     */
    default void loadWorldFromSeed(String worldName, long seed) {
    }


    /**
     * Creates a new world
     *
     * @param worldName   The name of the world
     * @param environment The world environment
     */
    default void createWorld(String worldName, World.Environment environment) {
    }


    /**
     * Creates a world with a specific seed
     *
     * @param worldName   The name of the world
     * @param environment The world environment
     * @param seed        The world seed
     */
    default void createWorldFromSeed(String worldName, World.Environment environment, long seed) {
    }


    /**
     * Loads an existing world
     *
     * @param worldName The name of the world to load
     */
    default void loadWorld(String worldName) {
    }


    /**
     * Checks if a world exists on disk
     *
     * @param worldName The name of the world to check
     * @return true if the world exists
     */
    default boolean worldExists(String worldName) {
        return false;
    }


    /**
     * Checks if a world is currently loaded
     *
     * @param worldName The name of the world to check
     * @return true if the world is loaded
     */
    default boolean isWorldLoaded(String worldName) {
        return false;
    }


    /**
     * Unloads a world
     *
     * @param worldName The name of the world to unload
     * @param save      Save the world before unloading
     */
    default void unloadWorld(String worldName, boolean save) {
    }

    /**
     * Deletes a world from disk
     *
     * @param worldName The name of the world to delete
     */
    default void deleteWorld(String worldName) {
    }

    /**
     * Recursively deletes a directory
     *
     * @param file The directory to delete
     */
    default void deleteDirectory(File file) {
    }

    /**
     * Saves world spawn location to database
     *
     * @param gameType The game type
     * @param world    The world name
     * @param x        X coordinate
     * @param y        Y coordinate
     * @param z        Z coordinate
     * @param pitch    Pitch angle
     * @param yaw      Yaw angle
     * @throws SQLException if database error occurs
     */
    default void saveWorldSpawn(String gameType, String world, double x, double y, double z, float pitch, float yaw) throws SQLException {
    }

    /**
     * Gets the spawn location for a world
     *
     * @param world The world name
     * @return The spawn location
     */
    default Location getSpawn(String world) {
        return new Location(null, 0, 0, 0);
    }


    /**
     * Checks if a world is the spawn world
     *
     * @param worldName The world name to check
     * @return true if it's the spawn world
     */
    default boolean isSpawnWorld(String worldName) {
        return false;
    }

    /**
     * Gets the spawn world name from the database
     *
     * @return The spawn world name
     */
    default String getSpawnWorld() {
        return "";
    }

    /**
     * Sets whether a world is the spawn world
     *
     * @param spawn 1 for spawn world, 0 for not spawn world
     * @param world The world name
     * @throws SQLException if database error occurs
     */
    default void setIsSpawn(int spawn, String world) throws SQLException {

    }
}
