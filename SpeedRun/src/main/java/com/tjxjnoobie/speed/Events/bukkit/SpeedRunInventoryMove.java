package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.IGameState;
import com.tjxjnoobie.api.interfaces.ISpeedRunContext;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class SpeedRunInventoryMove implements Listener {

    private final ISpeedRunContext speedRunContext;

    public SpeedRunInventoryMove(ISpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;
    }

    @EventHandler
    public void onInvMove(InventoryMoveItemEvent e){
        IGameState gameState = speedRunContext.getGameState();
        GameStateEnum currentState = gameState.getCurrentState();
        if(currentState == GameStateEnum.LOBBY || currentState == GameStateEnum.ENDING){
            e.setCancelled(true);
        }
    }
}
