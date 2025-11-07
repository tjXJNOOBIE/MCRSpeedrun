package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.enums.GameModeEnum;
import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class SpeedRunDeathEvent implements Listener, IGameMode, IGameState, IMCUtils, IPlayerManager {



    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String name = player.getName();

        GameModeEnum currentGamemode = getCurrentGameMode();
        GameStateEnum currentState = getCurrentState();

        if(currentGamemode == GameModeEnum.ELIMINATION
        && currentState == GameStateEnum.INGAME){
            eliminatePlayer(uuid,name,player);
        }else{
            e.setCancelled(true);
            sendDebugMessage(player,name+"Tried to die outside of INGAME state.");
        }
    }
}
