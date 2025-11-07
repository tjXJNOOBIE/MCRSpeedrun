package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.IGameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class SpeedRunMoveEvent implements Listener, IGameManager {



    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        double toX = e.getTo().getX();
        double toY = e.getTo().getY();
        double toZ = e.getTo().getZ();
        double fromX = e.getFrom().getX();
        double fromY = e.getFrom().getY();
        double fromZ = e.getFrom().getZ();
        boolean canMove = canMove();
        boolean InGamePlayer = getPlaying().containsKey(uuid);
        GameStateEnum currentState = getCurrentState();
        if(currentState == GameStateEnum.PREGAME){

            if(toX != fromX || toY != fromY||
                    toZ != fromZ && !canMove && InGamePlayer){
                e.setCancelled(true);
            }
        }
    }
}
