package com.tjxjnoobie.speed.cache;

import com.tjxjnoobie.API.utils.Utils;
import com.tjxjnoobie.API.minecraft.managers.WorldManager;
import com.tjxjnoobie.API.managers.GameState;
import com.tjxjnoobie.enums.GameStateEnum;
import com.tjxjnoobie.speed.managers.SpeedRunContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;

public class LocationCache {


    private final HashMap<String, Location> spawn = new HashMap<>();
    private final SpeedRunContext speedRunContext;

    public LocationCache(SpeedRunContext speedRunContext) {

        this.speedRunContext = speedRunContext;
        WorldManager worldManager = speedRunContext.getWorldManager();
        Utils utils = speedRunContext.getUtils();
        GameState gameState = speedRunContext.getGameState();
        Plugin plugin = speedRunContext.getPlugin();
        new BukkitRunnable(){
            @Override
            public void run() {
                Bukkit.getLogger().info("Adding world " + worldManager.getSpawnWorld());
                spawn.put(worldManager.getSpawnWorld(), worldManager.getSpawn(worldManager.getSpawnWorld()));
                Bukkit.getLogger().info(spawn.get(worldManager.getSpawnWorld())+ " Updated");
                try {
                    gameState.setGameState(GameStateEnum.LOBBY,utils.getServerID());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskLater(plugin, 100L);
    }




    public void loadLocationCache() {
        WorldManager worldManager = speedRunContext.getWorldManager();

        spawn.put(worldManager.getSpawnWorld(), worldManager.getSpawn(worldManager.getSpawnWorld()));



    }
    public void removeLocationCache(String worldName){
        spawn.remove(worldName);
    }

    public Location getSpawn() {
        WorldManager worldManager = speedRunContext.getWorldManager();
            return spawn.get(worldManager.getSpawnWorld());

    }
}
