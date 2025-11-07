package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.IGameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class SpeedRunInventoryMove implements Listener, IGameState {



    @EventHandler
    public void onInvMove(InventoryMoveItemEvent e){
        GameStateEnum currentState = getCurrentState();
        if(currentState == GameStateEnum.LOBBY || currentState == GameStateEnum.ENDING){
            e.setCancelled(true);
        }
    }
}
