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

public class SpeedRunQuitEvent implements Listener, IMCUtils, IGameManager {


    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        //TODO: Remove these delegations

        UUID uuid = e.getPlayer().getUniqueId();
        Player player = e.getPlayer();
        String name = e.getPlayer().getName();
        String displayName = String.valueOf(e.getPlayer().displayName());
        Location quitLocation = e.getPlayer().getLocation();
        int maxPlayers = getMaxPlayers();
        int playerCount = getPlayersRemaining();
        boolean QuitPlayer = getQuitPlayers().containsKey(uuid);
        GameStateEnum currentState = getCurrentState();
        boolean InGamePlayer = getPlaying().containsKey(uuid);


        if(currentState == GameStateEnum.LOBBY){
            getPlaying().remove(uuid);
            getAllPlayersHash().remove(uuid);
            getInNetherHash().remove(uuid);
            getInEnd().remove(uuid);
            getWatching().remove(uuid);
            //TODO Re-add bossbar cancel timer for speedrun lobby
            removePlayer(player);
            removePlayer(player);
            Bukkit.broadcastMessage(getMinecraftPrefix()+displayName+ " has left (§c"+playerCount+"§7/§c"+maxPlayers+"§7)");


        }else if(currentState == GameStateEnum.INGAME || currentState == GameStateEnum.PREGAME){

            if(InGamePlayer) {
                // Assign player to Quit Player
                Bukkit.broadcastMessage(getMinecraftPrefix()+displayName+ " has left. They have 5 minutes to rejoin");
                getQuitPlayers().put(uuid, name);
                QuitLocation().put(uuid, quitLocation);
                getPlaying().remove(uuid);
                getAllPlayersHash().remove(uuid);
            }else{
                // Player is spectating
                getWatching().remove(uuid);
                getAllPlayersHash().remove(uuid);
                Bukkit.broadcastMessage(getMinecraftPrefix()+displayName+ " has left");


            }


            }else{
            //Handle End game condition
            Bukkit.broadcastMessage(getMinecraftPrefix()+displayName+ " has left");
            getWatching().remove(uuid);
            getAllPlayersHash().remove(uuid);
            getPlaying().remove(uuid);
        }
        }
    }

