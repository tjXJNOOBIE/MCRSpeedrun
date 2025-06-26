package com.tjxjnoobie.speed.Events;

import com.tjxjnoobie.API.managers.GameMode;
import com.tjxjnoobie.API.managers.GameState;
import com.tjxjnoobie.API.minecraft.utils.MCUtils;
import com.tjxjnoobie.API.utils.Utils;
import com.tjxjnoobie.enums.GameModeEnum;
import com.tjxjnoobie.enums.GameStateEnum;
import com.tjxjnoobie.interfaces.*;
import com.tjxjnoobie.speed.managers.GameManager;
import com.tjxjnoobie.speed.managers.PlayerManager;
import com.tjxjnoobie.speed.managers.SpeedRunContext;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DeathEvent implements Listener {
    private  final SpeedRunContext speedRunContext;

    public DeathEvent(SpeedRunContext speedRunContext) {
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
            mcUtils.sendDebugMessage(player);
        }
    }
}
