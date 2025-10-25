package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.enums.GameModeEnum;
import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class SpeedRunDeathEvent implements Listener {
    private  final ISpeedRunContext speedRunContext;
    private IGlobalContext globalContext;
    public SpeedRunDeathEvent(ISpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;
    }


    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        IGameMode gameMode = speedRunContext.getGameMode();
        IGameManager gameManager = speedRunContext.getGameManager();
        IGameState gameState = speedRunContext.getGameState();
        IPlayerManager playerManager = speedRunContext.getPlayerManager();
        GameModeEnum currentGamemode = gameMode.getCurrentGameMode();
        GameStateEnum currentState = gameState.getCurrentState();
        IMCUtils mcUtils = speedRunContext.getMcUtils();
        if(currentGamemode == GameModeEnum.ELIMINATION
        && currentState == GameStateEnum.INGAME){
            playerManager.eliminatePlayer(uuid,name,player);
        }else{
            e.setCancelled(true);
            mcUtils.sendDebugMessage(player,name+"Tried to die outside of INGAME state.");
        }
    }
}
