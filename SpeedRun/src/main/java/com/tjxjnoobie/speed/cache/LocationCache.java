package com.tjxjnoobie.speed.cache;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.IGameState;
import com.tjxjnoobie.api.interfaces.ILocationCache;
import com.tjxjnoobie.api.interfaces.IUtils;
import com.tjxjnoobie.api.interfaces.IWorldManager;
import com.tjxjnoobie.api.platform.global.annotations.PostConstruct;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;


public class LocationCache implements IUtils, ILocationCache, IWorldManager, IGameState {


    private final HashMap<String, Location> spawn = new HashMap<>();
    private Plugin plugin;


    @PostConstruct
    private void init() {
        loadLocationCache();
    }




    public void loadLocationCache() {

            new BukkitRunnable(){
                @Override
                public void run() {
                    Bukkit.getLogger().info("Adding world " + getSpawnWorld());
                    spawn.put(getSpawnWorld(), getSpawn(getSpawnWorld()));
                    Bukkit.getLogger().info(spawn.get(getSpawnWorld())+ " Updated");
                    try {
                        setGameState(GameStateEnum.LOBBY,getServerID());
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
            return spawn.get(getSpawnWorld());

    }
}

