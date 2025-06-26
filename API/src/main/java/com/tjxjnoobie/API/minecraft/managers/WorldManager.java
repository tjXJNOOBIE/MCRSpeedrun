package com.tjxjnoobie.API.minecraft.managers;

import com.tjxjnoobie.API.GlobalContext;
import com.tjxjnoobie.API.managers.MySQL;
import com.tjxjnoobie.API.minecraft.utils.MCUtils;
import com.tjxjnoobie.API.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class WorldManager {



    private final GlobalContext globalContext;

    public WorldManager(GlobalContext globalContext) {
        this.globalContext = globalContext;
    }

    public void loadWorldFromSeed(String worldName, long seed) {
        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.seed(seed);
        worldCreator.environment(World.Environment.NORMAL);
        World world = Bukkit.createWorld(worldCreator);
        Bukkit.getLogger().info("World " + worldName + " created with seed " + seed);
    }

    public void createWorld(String worldName, World.Environment environment) {
        MCUtils mcUtils = globalContext.getMcUtils();
        Utils utils = globalContext.getUtils();
        World checkWorld = Bukkit.getWorld(worldName);
        if (worldExists(worldName)) {
            Bukkit.getLogger().info(worldName + " already exist on disk");
               mcUtils.sendDebugMessage (mcUtils.getAllPlayers(), utils.staffPrefix + "World exist on disk, loading...");

            loadWorld(worldName);
            return;
        }
        if (checkWorld != null) {
            Bukkit.getLogger().info("World already loaded");
        } else {
            WorldCreator worldCreator = new WorldCreator(worldName).environment(environment);
            World world = Bukkit.createWorld(worldCreator);
                mcUtils.sendDebugMessage(mcUtils.getAllPlayers(), utils.staffPrefix + "New World " + worldName + " created in " + environment.toString());

            Bukkit.getLogger().info(environment.toString() + " created with the name " + worldName);

        }
    }

    public void createWorldFromSeed(String worldName, World.Environment environment, Long seed) {
        MCUtils mcUtils = globalContext.getMcUtils();
        Utils utils = globalContext.getUtils();
        World checkWorld = Bukkit.getWorld(worldName);
        Player aplayers = mcUtils.getAllPlayers();
        UUID auuid = mcUtils.getAllPlayers().getUniqueId();

            if (worldExists(worldName)) {
                    Bukkit.getLogger().info(worldName + " World exist on disk");
                    mcUtils.sendDebugMessage(aplayers,utils.staffPrefix + "World exist on disk, loading...");

                    loadWorld(worldName);


            }
            if (checkWorld != null) {
                Bukkit.getLogger().info("World already loaded");
            } else {
                    WorldCreator worldCreator = new WorldCreator(worldName).environment(environment);
                    worldCreator.seed(seed);
                    World world = Bukkit.createWorld(worldCreator);
                    mcUtils.sendDebugMessage(aplayers, "New World " + worldName + " created in " + environment.toString());
                    Bukkit.getLogger().info(environment.toString() + " created with the name " + worldName);

            }

    }

    public void loadWorld(String worldName) {
        MCUtils mcUtils = globalContext.getMcUtils();
        Utils utils = globalContext.getUtils();
        WorldCreator worldCreator = new WorldCreator(worldName);
        World world = Bukkit.getWorld(worldName);
        Player aplayers = mcUtils.getAllPlayers();
        UUID auuid = mcUtils.getAllPlayers().getUniqueId();
            if (isWorldLoaded(worldName)) {

                mcUtils.sendDebugMessage(aplayers,

 "World is already loaded");

            }
            if (!worldExists(worldName)) {
                    mcUtils.sendDebugMessage(aplayers,

 "World does not exist");

            } else {
                mcUtils.sendDebugMessage(aplayers,

 "Loading world " + worldName);
                world = Bukkit.createWorld(worldCreator);
                mcUtils.sendDebugMessage(aplayers,

 "World loaded ");

            }
        }


    public boolean worldExists(String worldName) {
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        return worldFolder.exists() && worldFolder.isDirectory();
    }

    public boolean isWorldLoaded(String worldName) {
        World world = Bukkit.getWorld(worldName);
        return world != null;
    }

    public void unloadWorld(String worldName, boolean save) {
        MCUtils mcUtils = globalContext.getMcUtils();
        World world = Bukkit.getWorld(worldName);
        Player aplayers = mcUtils.getAllPlayers();
        UUID auuid = mcUtils.getAllPlayers().getUniqueId();

        if (!worldExists(worldName)) {
            mcUtils.sendDebugMessage(aplayers, "World does not exist");
            return;

        }
        if (world != null) {
            boolean unloaded = Bukkit.unloadWorld(world, save);

                if (unloaded) {
                    Bukkit.getLogger().info("World '" + worldName + "' has been unloaded.");

                        mcUtils.sendDebugMessage(aplayers, "World '" + worldName + "' has been unloaded.");

                } else {
                    Bukkit.getLogger().info("Failed to unload world '" + worldName + "'.");
                        mcUtils.sendDebugMessage(aplayers,"Failed to unload world '" + worldName + "'.");


                }

        } else {
            Bukkit.getLogger().info("World '" + worldName + "' is not loaded.");
                mcUtils.sendDebugMessage(aplayers,"World '" + worldName + "' is not loaded.");

        }
    }
    public void deleteWorld(String worldName) {
        // Unload the world
        MCUtils mcUtils = globalContext.getMcUtils();
        Player aplayers = mcUtils.getAllPlayers();
        World world = Bukkit.getWorld(worldName);

            if (world != null) {
                mcUtils.sendDebugMessage(aplayers, "World found! Unloading...");
                unloadWorld(worldName, false);// Unload world without saving
            }else{
                mcUtils.sendDebugMessage(aplayers,worldName+" does not exist");
                return;
            }
        mcUtils.sendDebugMessage(aplayers, "Deleting World....");
        Bukkit.getLogger().info("Deleting world... " +worldName);
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        deleteDirectory(worldFolder);
        Bukkit.getLogger().info(worldName+  " Deleted");
        mcUtils.sendDebugMessage(aplayers, worldName+ " Deleted");

    }

    public void deleteDirectory(File file) {
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                deleteDirectory(subFile);
            }
        }
        file.delete();
    }
    public void saveWorldSpawn(String gameType, String world, double x, double y, double z, float pitch, float yaw) throws SQLException {
        MySQL.executePreparedStatement("DELETE FROM world_data WHERE world= ?", world);
        MySQL.executePreparedStatement( "REPLACE INTO world_data (world, X, Y, Z, PITCH, YAW, GAMETYPE) VALUES (?, ?, ?, ?, ?, ?, ?)", world, x, y, z, pitch, yaw,gameType);

    }

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

    public String getSpawnWorld() {
        Utils utils = globalContext.getUtils();
        try {
            String serverID = utils.getServerID();
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

    public void setIsSpawn(int spawn, String world) throws SQLException {
        MySQL.executePreparedStatement("UPDATE world_data SET IS_SPAWN = ? WHERE WORLD = ?",spawn,world);
    }
}




