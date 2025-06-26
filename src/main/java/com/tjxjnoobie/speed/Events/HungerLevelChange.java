package com.tjxjnoobie.speed.Events;

import com.tjxjnoobie.API.managers.GameState;
import com.tjxjnoobie.enums.GameStateEnum;
import com.tjxjnoobie.speed.managers.SpeedRunContext;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.entity.Player;

public class HungerLevelChange implements Listener {

    private final SpeedRunContext speedRunContext;

    public HungerLevelChange(SpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        GameState gameState = speedRunContext.getGameState();
        if (event.getEntity() instanceof Player) {
            GameStateEnum currentState = gameState.getCurrentState();
            if (currentState == GameStateEnum.LOBBY || currentState == GameStateEnum.PREGAME || currentState == GameStateEnum.ENDING) {
                event.setCancelled(true);
            }
        }
    }
}