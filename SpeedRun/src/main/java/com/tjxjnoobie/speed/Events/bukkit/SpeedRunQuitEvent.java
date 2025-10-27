package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class SpeedRunQuitEvent implements Listener, IMCUtils {


    private final ISpeedRunContext speedRunContext;
    public SpeedRunQuitEvent(ISpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        //TODO: Remove these delegations
        IGameManager gameManager = speedRunContext.getGameManager();
        IGameState gameState = speedRunContext.getGameState();
        IMCUtils mcUtils = speedRunContext.getMcUtils();
        ISpeedRunJoinEvent joinEvent = speedRunContext.getJoinEvent();
        IBossBarManager bossBarManager = speedRunContext.getBossBarManager();
        IStatsManager statsManager = speedRunContext.getStatsManager();
        ISpeedrunStatsCache srStatsCache = speedRunContext.getSRStatsCache();
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
            //TODO Re-add bossbar cancel timer for speedrun lobby
            bossBarManager.removePlayer(player);
            bossBarManager.removePlayer(player);
            Bukkit.broadcastMessage(getMinecraftPrefix()+displayName+ " has left (§c"+playerCount+"§7/§c"+maxPlayers+"§7)");


        }else if(currentState == GameStateEnum.INGAME || currentState == GameStateEnum.PREGAME){

            if(InGamePlayer) {
                // Assign player to Quit Player
                Bukkit.broadcastMessage(getMinecraftPrefix()+displayName+ " has left. They have 5 minutes to rejoin");
                gameManager.getQuitPlayers().put(uuid, name);
                gameManager.QuitLocation().put(uuid, quitLocation);
                gameManager.getPlaying().remove(uuid);
                gameManager.getAllPlayersHash().remove(uuid);
            }else{
                // Player is spectating
                gameManager.getWatching().remove(uuid);
                gameManager.getAllPlayersHash().remove(uuid);
                Bukkit.broadcastMessage(getMinecraftPrefix()+displayName+ " has left");


            }


            }else{
            //Handle End game condition
            Bukkit.broadcastMessage(getMinecraftPrefix()+displayName+ " has left");
            gameManager.getWatching().remove(uuid);
            gameManager.getAllPlayersHash().remove(uuid);
            gameManager.getPlaying().remove(uuid);
        }
        }
    }

