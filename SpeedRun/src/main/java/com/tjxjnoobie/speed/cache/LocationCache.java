package com.tjxjnoobie.speed.cache;

import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.global.annotations.PostConstruct;
import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.speed.managers.SpeedRunContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;


public class LocationCache implements IUtils, ILocationCache {


    private final HashMap<String, Location> spawn = new HashMap<>();
    @Inject private SpeedRunContext speedRunContext;
    @Inject private IGlobalContext globalContext;
    @Inject private IGameState gameState;
    @Inject private IWorldManager worldManager;
    private Plugin plugin;


    @PostConstruct
    private void init() {
        loadLocationCache();
    }




    public void loadLocationCache() {

            new BukkitRunnable(){
                @Override
                public void run() {
                    Bukkit.getLogger().info("Adding world " + worldManager.getSpawnWorld());
                    spawn.put(worldManager.getSpawnWorld(), worldManager.getSpawn(worldManager.getSpawnWorld()));
                    Bukkit.getLogger().info(spawn.get(worldManager.getSpawnWorld())+ " Updated");
                    try {
                        gameState.setGameState(GameStateEnum.LOBBY,getServerID());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }.runTaskLater(plugin, 100L);
        }



    public void removeLocationCache(String worldName){
        spawn.remove(worldName);
    }

    public Location getSpawn() {
        IWorldManager worldManager = speedRunContext.getWorldManager();
            return spawn.get(worldManager.getSpawnWorld());

    }
}

