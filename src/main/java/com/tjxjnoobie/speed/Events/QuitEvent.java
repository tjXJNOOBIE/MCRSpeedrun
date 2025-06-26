package com.tjxjnoobie.speed.Events;

import com.tjxjnoobie.API.cache.SpeedrunStatsCache;
import com.tjxjnoobie.API.managers.GameState;
import com.tjxjnoobie.API.managers.StatsManager;
import com.tjxjnoobie.API.utils.Utils;
import com.tjxjnoobie.API.cache.RatingCache;
import com.tjxjnoobie.API.minecraft.utils.MCUtils;
import com.tjxjnoobie.enums.GameStateEnum;
import com.tjxjnoobie.interfaces.IGameManager;
import com.tjxjnoobie.speed.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class QuitEvent implements Listener {


    private final SpeedRunContext speedRunContext;
    public QuitEvent(SpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        IGameManager gameManager = speedRunContext.getGameManager();
        GameState gameState = speedRunContext.getGameState();
        MCUtils mcUtils = speedRunContext.getMcUtils();
        JoinEvent joinEvent = speedRunContext.getJoinEvent();
        BossBarManager bossBarManager = speedRunContext.getBossBarManager();
        Utils utils = speedRunContext.getUtils();
        StatsManager statsManager = speedRunContext.getStatsManager();
        SpeedrunStatsCache srStatsCache = speedRunContext.getSRStatsCache();
        UUID uuid = e.getPlayer().getUniqueId();
        Player player = e.getPlayer();
        String name = e.getPlayer().getName();
        String displayName = String.valueOf(e.getPlayer().displayName());
        Location quitLocation = e.getPlayer().getLocation();
        int maxPlayers = gameManager.getMaxPlayers();
        int playerCount = gameManager.getPlayersRemaining();
        boolean QuitPlayer = gameManager.getQuitPlayers().containsKey(uuid);
        GameStateEnum currentState = gameState.getCurrentState();
        boolean InGamePlayer = gameManager.getPlaying().containsKey(uuid);


        if(currentState == GameStateEnum.LOBBY){
            gameManager.getPlaying().remove(uuid);
            gameManager.getAllPlayersHash().remove(uuid);
            gameManager.getInNetherHash().remove(uuid);
            gameManager.getInEnd().remove(uuid);
            gameManager.getWatching().remove(uuid);
            mcUtils.cancelTask(joinEvent.bossBar);
            bossBarManager.removePlayer(player);
            Bukkit.broadcastMessage(utils.prefix+displayName+ " has left (§c"+playerCount+"§7/§c"+maxPlayers+"§7)");


        }else if(currentState == GameStateEnum.INGAME || currentState == GameStateEnum.PREGAME){

            if(InGamePlayer) {
                // Assign player to Quit Player
                Bukkit.broadcastMessage(utils.prefix+displayName+ " has left. They have 5 minutes to rejoin");
                gameManager.getQuitPlayers().put(uuid, name);
                gameManager.QuitLocation().put(uuid, quitLocation);
                gameManager.getPlaying().remove(uuid);
                gameManager.getAllPlayersHash().remove(uuid);
            }else{
                // Player is spectating
                gameManager.getWatching().remove(uuid);
                gameManager.getAllPlayersHash().remove(uuid);
                Bukkit.broadcastMessage(utils.prefix+displayName+ " has left");


            }


            }else{
            //Handle End game condition
            Bukkit.broadcastMessage(utils.prefix+displayName+ " has left");
            gameManager.getWatching().remove(uuid);
            gameManager.getAllPlayersHash().remove(uuid);
            gameManager.getPlaying().remove(uuid);
        }
        }
    }

