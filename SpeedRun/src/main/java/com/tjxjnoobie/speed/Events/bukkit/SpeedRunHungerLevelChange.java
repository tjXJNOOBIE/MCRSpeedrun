package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.IGameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class SpeedRunHungerLevelChange implements Listener, IGameState {


    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            GameStateEnum currentState = getCurrentState();
            if (currentState == GameStateEnum.LOBBY || currentState == GameStateEnum.PREGAME || currentState == GameStateEnum.ENDING) {
                event.setCancelled(true);
            }
        }
    }
}