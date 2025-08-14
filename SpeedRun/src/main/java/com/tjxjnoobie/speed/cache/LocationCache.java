package com.tjxjnoobie.speed.cache;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.IGameState;
import com.tjxjnoobie.api.interfaces.IGlobalContext;
import com.tjxjnoobie.api.interfaces.IUtils;
import com.tjxjnoobie.api.interfaces.IWorldManager;
import com.tjxjnoobie.speed.managers.SpeedRunContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;

public class LocationCache implements IUtils{


    private final HashMap<String, Location> spawn = new HashMap<>();
    private final SpeedRunContext speedRunContext;
    private IGlobalContext globalContext;

    public LocationCache(SpeedRunContext speedRunContext) {

        this.speedRunContext = speedRunContext;
        IWorldManager worldManager = speedRunContext.getWorldManager();
        IUtils utils = speedRunContext.getUtils();
        IGameState gameState = speedRunContext.getGameState();
        Plugin plugin = speedRunContext.getPlugin();
        new BukkitRunnable(){
            @Override
            public void run() {
                Bukkit.getLogger().info("Adding world " + worldManager.getSpawnWorld());
                spawn.put(worldManager.getSpawnWorld(), worldManager.getSpawn(worldManager.getSpawnWorld()));
                Bukkit.getLogger().info(spawn.get(worldManager.getSpawnWorld())+ " Updated");
                try {
                    gameState.setGameState(GameStateEnum.LOBBY,getServerID(globalContext));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskLater(plugin, 100L);
    }




    public void loadLocationCache() {
        IWorldManager worldManager = speedRunContext.getWorldManager();

        spawn.put(worldManager.getSpawnWorld(), worldManager.getSpawn(worldManager.getSpawnWorld()));



    }
    public void removeLocationCache(String worldName){
        spawn.remove(worldName);
    }

    public Location getSpawn() {
        IWorldManager worldManager = speedRunContext.getWorldManager();
            return spawn.get(worldManager.getSpawnWorld());

    }
}
