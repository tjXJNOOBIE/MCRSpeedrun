package com.tjxjnoobie.speed.Events;

import com.tjxjnoobie.API.managers.GameState;
import com.tjxjnoobie.enums.GameStateEnum;
import com.tjxjnoobie.speed.managers.SpeedRunContext;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class DropEvent implements Listener {

    private final SpeedRunContext speedRunContext;

    public DropEvent(SpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        GameState gameState = speedRunContext.getGameState();
        GameStateEnum currentState = gameState.getCurrentState();
        if(currentState == GameStateEnum.LOBBY){
            e.setCancelled(true);
        }
    }
}
