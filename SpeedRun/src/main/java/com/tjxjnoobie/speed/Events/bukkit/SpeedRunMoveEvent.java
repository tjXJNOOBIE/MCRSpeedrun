package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.IGameManager;
import com.tjxjnoobie.api.interfaces.IGameState;
import com.tjxjnoobie.speed.managers.SpeedRunContext;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class SpeedRunMoveEvent implements Listener {

    private final SpeedRunContext speedRunContext;

    public SpeedRunMoveEvent(SpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;
    }


    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        IGameState gameState = speedRunContext.getGameState();
        IGameManager gameManager = speedRunContext.getGameManager();
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        double toX = e.getTo().getX();
        double toY = e.getTo().getY();
        double toZ = e.getTo().getZ();
        double fromX = e.getFrom().getX();
        double fromY = e.getFrom().getY();
        double fromZ = e.getFrom().getZ();
        boolean canMove = gameManager.canMove();
        boolean InGamePlayer = gameManager.getPlaying().containsKey(uuid);
        GameStateEnum currentState = gameState.getCurrentState();
        if(currentState == GameStateEnum.PREGAME){

            if(toX != fromX || toY != fromY||
                    toZ != fromZ && !canMove && InGamePlayer){
                e.setCancelled(true);
            }
        }
    }
}
