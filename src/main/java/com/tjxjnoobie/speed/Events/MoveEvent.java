package com.tjxjnoobie.speed.Events;

import com.tjxjnoobie.API.managers.GameState;
import com.tjxjnoobie.enums.GameStateEnum;
import com.tjxjnoobie.speed.managers.GameManager;
import com.tjxjnoobie.speed.managers.SpeedRunContext;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class MoveEvent implements Listener {

    private final SpeedRunContext speedRunContext;

    public MoveEvent(SpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;
    }


    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        GameState gameState = speedRunContext.getGameState();
        GameManager gameManager = speedRunContext.getGameManager();
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
