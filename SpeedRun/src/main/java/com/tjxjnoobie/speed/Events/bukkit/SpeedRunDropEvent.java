package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.IGameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class SpeedRunDropEvent implements Listener, IGameState {



    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        GameStateEnum currentState = getCurrentState();
        if(currentState == GameStateEnum.LOBBY){
            e.setCancelled(true);
        }
    }
}
