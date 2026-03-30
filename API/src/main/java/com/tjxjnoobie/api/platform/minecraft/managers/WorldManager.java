package com.tjxjnoobie.api.platform.minecraft.managers;

import com.tjxjnoobie.api.interfaces.IWorldManager;
import com.tjxjnoobie.api.managers.MySQL;
import com.tjxjnoobie.api.platform.minecraft.utils.interfaces.IMCUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class WorldManager implements IMCUtils, IWorldManager {





    @Override
    public void loadWorldFromSeed(String worldName, long seed) {
        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.seed(seed);
        worldCreator.environment(World.Environment.NORMAL);
        World world = Bukkit.createWorld(worldCreator);
        Bukkit.getLogger().info("World " + worldName + " created with seed " + seed);
    }
    @Override
    public void createWorld(String worldName, World.Environment environment) {

        World checkWorld = Bukkit.getWorld(worldName);
        if (worldExists(worldName)) {
            Bukkit.getLogger().info(worldName + " already exist on disk");
            sendDebugMessage (getAllMinecraftPlayers(), getMinecraftStaffInGamePrefix() + "World exist on disk, loading...");
            loadWorld(worldName);
            return;
        }
        if (checkWorld != null) {
            Bukkit.getLogger().info("World already loaded");
        } else {
            WorldCreator worldCreator = new WorldCreator(worldName).environment(environment);
            World world = Bukkit.createWorld(worldCreator);
                sendDebugMessage(getAllMinecraftPlayers(), getMinecraftStaffInGamePrefix() + "New World " + worldName + " created in " + environment.toString());

            Bukkit.getLogger().info(environment.toString() + " created with the name " + worldName);

        }
    }
    @Override
    public void createWorldFromSeed(String worldName, World.Environment environment, long seed){

        World checkWorld = Bukkit.getWorld(worldName);
        Player aplayers = getAllMinecraftPlayers();
        UUID auuid = getAllMinecraftPlayers().getUniqueId();

            if (worldExists(worldName)) {
                    Bukkit.getLogger().info(worldName + " World exist on disk");
                    sendDebugMessage(aplayers,getMinecraftStaffInGamePrefix() + "World exist on disk, loading...");

                    loadWorld(worldName);


            }
            if (checkWorld != null) {
                Bukkit.getLogger().info("World already loaded");
            } else {
                    WorldCreator worldCreator = new WorldCreator(worldName).environment(environment);
                    worldCreator.seed(seed);
                    World world = Bukkit.createWorld(worldCreator);
                    sendDebugMessage(aplayers, "New World " + worldName + " created in " + environment.toString());
                    Bukkit.getLogger().info(environment.toString() + " created with the name " + worldName);

            }

    }
    @Override
    public void loadWorld(String worldName) {
        WorldCreator worldCreator = new WorldCreator(worldName);
        World world = Bukkit.getWorld(worldName);
        Player aplayers = getAllMinecraftPlayers();
        UUID auuid = getAllMinecraftPlayers().getUniqueId();
        if (isWorldLoaded(worldName)) {
            sendDebugMessage( aplayers, "World is already loaded");

        }
        if (!worldExists(worldName)) {
            sendDebugMessage(aplayers,

                    "World does not exist");

        } else {
            sendDebugMessage(aplayers,

                    "Loading world " + worldName);
            world = Bukkit.createWorld(worldCreator);
            sendDebugMessage(aplayers,

                    "World loaded ");

        }
    }

    @Override
    public boolean worldExists(String worldName) {
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        return worldFolder.exists() && worldFolder.isDirectory();
    }
    @Override
    public boolean isWorldLoaded(String worldName) {
        World world = Bukkit.getWorld(worldName);
        return world != null;
    }
    @Override
    public void unloadWorld(String worldName, boolean save) {
        World world = Bukkit.getWorld(worldName);
        Player aplayers = getAllMinecraftPlayers();
        UUID auuid = getAllMinecraftPlayers().getUniqueId();

        if (!worldExists(worldName)) {
            sendDebugMessage(aplayers, "World does not exist");
            return;

        }
        if (world != null) {
            boolean unloaded = Bukkit.unloadWorld(world, save);

                if (unloaded) {
                    Bukkit.getLogger().info("World '" + worldName + "' has been unloaded.");

                        sendDebugMessage(aplayers, "World '" + worldName + "' has been unloaded.");

                } else {
                    Bukkit.getLogger().info("Failed to unload world '" + worldName + "'.");
                       sendDebugMessage(aplayers,"Failed to unload world '" + worldName + "'.");


                }

        } else {
            Bukkit.getLogger().info("World '" + worldName + "' is not loaded.");
                sendDebugMessage(aplayers,"World '" + worldName + "' is not loaded.");

        }
    }
    @Override
    public void deleteWorld(String worldName) {
        // Unload the world
        Player aplayers = getAllMinecraftPlayers();
        World world = Bukkit.getWorld(worldName);

            if (world != null) {
                sendDebugMessage(aplayers, "World found! Unloading...");
                unloadWorld(worldName, false);// Unload world without saving
            }else{
                sendDebugMessage(aplayers,worldName+" does not exist");
                return;
            }
        sendDebugMessage(aplayers, "Deleting World....");
        Bukkit.getLogger().info("Deleting world... " +worldName);
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        deleteDirectory(worldFolder);
        Bukkit.getLogger().info(worldName+  " Deleted");
        sendDebugMessage(aplayers, worldName+ " Deleted");

    }
    @Override
    public void deleteDirectory(File file) {
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                deleteDirectory(subFile);
            }
        }
        file.delete();
    }
    @Override
    public void saveWorldSpawn(String gameType, String world, double x, double y, double z, float pitch, float yaw) throws SQLException {
        MySQL.executePreparedStatement("DELETE FROM world_data WHERE world= ?", world);
        MySQL.executePreparedStatement( "REPLACE INTO world_data (world, X, Y, Z, PITCH, YAW, GAMETYPE) VALUES (?, ?, ?, ?, ?, ?, ?)", world, x, y, z, pitch, yaw,gameType);

    }
    @Override
    public Location getSpawn(String world) {
        Integer i;
        try {
            World spawnWorld = Bukkit.getWorld(world);
            ResultSet rs = MySQL.getResult("SELECT * from world_data WHERE world = ?", world);
            if ((rs.next())) {
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
    @Override
    public boolean isSpawnWorld(String worldName) {
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
    @Override
    public String getSpawnWorld() {

        try {
            String serverID = getLocalServerID();
            String query = "SELECT SPAWNWORLD FROM servers WHERE SERVERID= ?";
            ResultSet rs = MySQL.getResult(query, serverID);
            if (rs.next()) {
                String spawnWorld = rs.getString("SPAWNWORLD");
                if(isWorldLoaded(spawnWorld)){
                    return rs.getString("SPAWNWORLD");
                }else{
                    return "Invalid world name";
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting spawn world "+e.getMessage());
            throw new RuntimeException(e);
        }

        return "Error";
    }
    @Override
    public void setIsSpawn(int spawn, String world) throws SQLException {
        MySQL.executePreparedStatement("UPDATE world_data SET IS_SPAWN = ? WHERE WORLD = ?",spawn,world);
    }


}