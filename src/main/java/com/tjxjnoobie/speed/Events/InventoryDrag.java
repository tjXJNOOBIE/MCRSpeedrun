package com.tjxjnoobie.speed.Events;

import com.tjxjnoobie.API.managers.GameState;
import com.tjxjnoobie.enums.GameStateEnum;
import com.tjxjnoobie.interfaces.IGameState;
import com.tjxjnoobie.speed.managers.SpeedRunContext;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

public class InventoryDrag implements Listener {

    private final SpeedRunContext speedRunContext;

    public InventoryDrag(SpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;
    }


    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        IGameState gameState = speedRunContext.getGameState();
        GameStateEnum currentState = gameState.getCurrentState();
        if(currentState == GameStateEnum.LOBBY || currentState == GameStateEnum.ENDING){
            e.setCancelled(true);
        }
        if (e.getView().getTitle().equals("§eVote for a Gamemode")) {
            e.setCancelled(true);

        }
    }
}
