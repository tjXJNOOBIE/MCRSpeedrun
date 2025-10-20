package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.managers.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interface for world management operations with default implementations
 * @param <T> The type of context this world manager works with
 */

public interface IWorldManager<T> extends IUtils<T>, IMCUtils{


    ;
    /**
     * Gets the global context for this world manager
     * @return The global context
     */
    @Inject
    default IGlobalContext getGlobalContext(){
        return null;
    }


    // Override this in implementations that need context

    
    /**
     * Loads a world from a seed
     * @param worldName The name of the world
     * @param seed The world seed
     */
    default void loadWorldFromSeed(String worldName, long seed) {
        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.seed(seed);
        worldCreator.environment(World.Environment.NORMAL);
        World world = Bukkit.createWorld(worldCreator);
        Bukkit.getLogger().info("World " + worldName + " created with seed " + seed);

    }
    
    /**
     * Creates a new world
     * @param worldName The name of the world
     * @param environment The world environment
     */
    default void createWorld(String worldName, World.Environment environment) {
        World checkWorld = Bukkit.getWorld(worldName);
        if (worldExists(worldName)) {
            Bukkit.getLogger().info(worldName + " already exist on disk");

            sendDebugMessage(getGlobalContext(), getAllPlayers(), staffPrefix + "World exist on disk, loading...");
        }
        loadWorld(worldName);

        if (checkWorld != null) {
            Bukkit.getLogger().info("World already loaded");
        } else {
            WorldCreator worldCreator = new WorldCreator(worldName).environment(environment);
            World world = Bukkit.createWorld(worldCreator);
            
                sendDebugMessage(getGlobalContext(), getAllPlayers(), staffPrefix + "New World " + worldName + " created in " + environment.toString());
            
            Bukkit.getLogger().info(environment.toString() + " created with the name " + worldName);
        }
    }
    
    /**
     * Creates a world with a specific seed
     * @param worldName The name of the world
     * @param environment The world environment
     * @param seed The world seed
     */
    default void createWorldFromSeed(String worldName, World.Environment environment, long seed) {
        World checkWorld = Bukkit.getWorld(worldName);
        Player aplayers = getAllPlayers();
        
        
        if (worldExists(worldName)) {
            Bukkit.getLogger().info(worldName + " World exist on disk");
            sendDebugMessage(getGlobalContext(), aplayers, staffPrefix + "World exist on disk, loading...");
            
            loadWorld(worldName);
            return;
        }
        if (checkWorld != null) {
            Bukkit.getLogger().info("World already loaded");
        } else {
            WorldCreator worldCreator = new WorldCreator(worldName).environment(environment);
            worldCreator.seed(seed);
            World world = Bukkit.createWorld(worldCreator);
            sendDebugMessage(getGlobalContext(), aplayers, "New World " + worldName + " created in " + environment.toString());
            
            Bukkit.getLogger().info(environment.toString() + " created with the name " + worldName);
        }
    }
    
    /**
     * Loads an existing world
     * @param worldName The name of the world to load
     */
    default void loadWorld(String worldName) {
        WorldCreator worldCreator = new WorldCreator(worldName);
        World world = Bukkit.getWorld(worldName);
        Player aplayers = getAllPlayers();
        
        
        if (isWorldLoaded(worldName)) {
            sendDebugMessage(getGlobalContext(), aplayers, "World is already loaded");
            
            return;
        }
        if (!worldExists(worldName)) {
            sendDebugMessage(getGlobalContext(), aplayers, "World does not exist");
            
        } else {
            sendDebugMessage(getGlobalContext(), aplayers, "Loading world " + worldName);
            
            world = Bukkit.createWorld(worldCreator);
            sendDebugMessage(getGlobalContext(), aplayers, "World loaded ");
            
        }
    }
    
    /**
     * Checks if a world exists on disk
     * @param worldName The name of the world to check
     * @return true if the world exists
     */
    default boolean worldExists(String worldName) {
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        return worldFolder.exists() && worldFolder.isDirectory();
    }
    
    /**
     * Checks if a world is currently loaded
     * @param worldName The name of the world to check
     * @return true if the world is loaded
     */
    default boolean isWorldLoaded(String worldName) {
        World world = Bukkit.getWorld(worldName);
        return world != null;
    }
    
    /**
     * Unloads a world
     * @param worldName The name of the world to unload
     * @param save Save the world before unloading
     */
    default void unloadWorld(String worldName, boolean save) {
        World world = Bukkit.getWorld(worldName);
        Player aplayers = getAllPlayers();
        
        
        if (!worldExists(worldName)) {
            if (getGlobalContext() != null && aplayers != null) {
                sendDebugMessage(getGlobalContext(), aplayers, "World does not exist");
            }
            return;
        }
        if (world != null) {
            boolean unloaded = Bukkit.unloadWorld(world, save);
            
            if (unloaded) {
                Bukkit.getLogger().info("World '" + worldName + "' has been unloaded.");
                if (getGlobalContext() != null && aplayers != null) {
                    sendDebugMessage(getGlobalContext(), aplayers, "World '" + worldName + "' has been unloaded.");
                }
            } else {
                Bukkit.getLogger().info("Failed to unload world '" + worldName + "'.");
                if (getGlobalContext() != null && aplayers != null) {
                    sendDebugMessage(getGlobalContext(), aplayers, "Failed to unload world '" + worldName + "'.");
                }
            }
        } else {
            Bukkit.getLogger().info("World '" + worldName + "' is not loaded.");
            if (getGlobalContext() != null && aplayers != null) {
                sendDebugMessage(getGlobalContext(), aplayers, "World '" + worldName + "' is not loaded.");
            }
        }
    }
    
    /**
     * Deletes a world from disk
     * @param worldName The name of the world to delete
     */
    default void deleteWorld(String worldName) {
        Player aplayers = getAllPlayers();
        World world = Bukkit.getWorld(worldName);
        
        
        if (world != null) {
            if (getGlobalContext() != null && aplayers != null) {
                sendDebugMessage(getGlobalContext(), aplayers, "World found! Unloading...");
            }
            unloadWorld(worldName, false); // Unload world without saving
        } else {
            if (getGlobalContext() != null && aplayers != null) {
                sendDebugMessage(getGlobalContext(), aplayers, worldName + " does not exist");
            }
            return;
        }
        if (getGlobalContext() != null && aplayers != null) {
            sendDebugMessage(getGlobalContext(), aplayers, "Deleting World....");
        }
        Bukkit.getLogger().info("Deleting world... " + worldName);
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        deleteDirectory(worldFolder);
        Bukkit.getLogger().info(worldName + " Deleted");
        if (getGlobalContext() != null && aplayers != null) {
            sendDebugMessage(getGlobalContext(), aplayers, worldName + " Deleted");
        }
    }
    
    /**
     * Recursively deletes a directory
     * @param file The directory to delete
     */
    default void deleteDirectory(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    deleteDirectory(subFile);
                }
            }
        }
        file.delete();
    }
    
    /**
     * Saves world spawn location to database
     * @param gameType The game type
     * @param world The world name
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param pitch Pitch angle
     * @param yaw Yaw angle
     * @throws SQLException if database error occurs
     */
    default void saveWorldSpawn(String gameType, String world, double x, double y, double z, float pitch, float yaw) throws SQLException {
        MySQL.executePreparedStatement("DELETE FROM world_data WHERE world= ?", world);
        MySQL.executePreparedStatement("REPLACE INTO world_data (world, X, Y, Z, PITCH, YAW, GAMETYPE) VALUES (?, ?, ?, ?, ?, ?, ?)", 
            world, x, y, z, pitch, yaw, gameType);
    }
    
    /**
     * Gets the spawn location for a world
     * @param world The world name
     * @return The spawn location
     */
    default Location getSpawn(String world) {
        try {
            World spawnWorld = Bukkit.getWorld(world);
            ResultSet rs = MySQL.getResult("SELECT * from world_data WHERE world = ?", world);
            if (rs.next()) {
                String worldName = rs.getString("world");
                double x = rs.getDouble("X");
                double y = rs.getDouble("Y");
                double z = rs.getDouble("Z");
                float pitch = rs.getFloat("PITCH");
                float yaw = rs.getFloat("YAW");
                
                return new Location(spawnWorld, x, y, z, pitch, yaw);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Checks if a world is the spawn world
     * @param worldName The world name to check
     * @return true if it's the spawn world
     */
    default boolean isSpawnWorld(String worldName) {
        try {
            String query = "SELECT IS_SPAWN FROM world_data WHERE world ='" + worldName + "'";
            ResultSet rs = MySQL.getResult(query);
            if (rs.next()) {
                return rs.getBoolean("IS_SPAWN");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    
    /**
     * Gets the spawn world name from the database
     * @return The spawn world name
     */
    default String getSpawnWorld() {
        try {
            
            String serverID = getGlobalContext() != null ? getServerID() : null;
            if (serverID == null) {
                return "Error: No server ID";
            }
            String query = "SELECT SPAWNWORLD FROM servers WHERE SERVERID= ?";
            ResultSet rs = MySQL.getResult(query, serverID);
            if (rs.next()) {
                String spawnWorld = rs.getString("SPAWNWORLD");
                if (isWorldLoaded(spawnWorld)) {
                    return spawnWorld;
                } else {
                    return "Invalid world name";
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting spawn world " + e.getMessage());
            throw new RuntimeException(e);
        }
        return "Error";
    }
    
    /**
     * Sets whether a world is the spawn world
     * @param spawn 1 for spawn world, 0 for not spawn world
     * @param world The world name
     * @throws SQLException if database error occurs
     */
    default void setIsSpawn(int spawn, String world) throws SQLException {
        MySQL.executePreparedStatement("UPDATE world_data SET IS_SPAWN = ? WHERE WORLD = ?", spawn, world);
    }
}
