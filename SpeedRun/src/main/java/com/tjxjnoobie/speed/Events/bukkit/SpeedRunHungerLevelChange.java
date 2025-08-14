package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.IGameState;
import com.tjxjnoobie.api.interfaces.ISpeedRunContext;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class SpeedRunHungerLevelChange implements Listener {

    private final ISpeedRunContext speedRunContext;

    public SpeedRunHungerLevelChange(ISpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        IGameState gameState = speedRunContext.getGameState();
        if (event.getEntity() instanceof Player) {
            GameStateEnum currentState = gameState.getCurrentState();
            if (currentState == GameStateEnum.LOBBY || currentState == GameStateEnum.PREGAME || currentState == GameStateEnum.ENDING) {
                event.setCancelled(true);
            }
        }
    }
}