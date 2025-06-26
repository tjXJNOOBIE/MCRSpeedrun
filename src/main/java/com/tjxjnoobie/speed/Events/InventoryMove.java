package com.tjxjnoobie.speed.Events;

import com.tjxjnoobie.API.managers.GameState;
import com.tjxjnoobie.enums.GameStateEnum;
import com.tjxjnoobie.speed.managers.SpeedRunContext;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class InventoryMove implements Listener {

    private final SpeedRunContext speedRunContext;

    public InventoryMove(SpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;
    }

    @EventHandler
    public void onInvMove(InventoryMoveItemEvent e){
        GameState gameState = speedRunContext.getGameState();
        GameStateEnum currentState = gameState.getCurrentState();
        if(currentState == GameStateEnum.LOBBY || currentState == GameStateEnum.ENDING){
            e.setCancelled(true);
        }
    }
}
